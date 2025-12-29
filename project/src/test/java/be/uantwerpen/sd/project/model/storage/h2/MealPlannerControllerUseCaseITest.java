package be.uantwerpen.sd.project.model.storage.h2;

import static org.junit.jupiter.api.Assertions.*;

import be.uantwerpen.sd.project.controller.MealPlannerController;
import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.grocery.GroceryItem;
import be.uantwerpen.sd.project.model.grocery.GroceryListService;
import be.uantwerpen.sd.project.model.grocery.MetricGroceryListStrategy;
import be.uantwerpen.sd.project.model.storage.GroceryDeltaRepository;
import be.uantwerpen.sd.project.model.storage.MealPlanRepository;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MealPlannerControllerUseCaseITest {

    @Test
    void realisticUseCase_controllerToModel_persistsMealPlanAndGroceryDeltas() throws Exception {
        /*
         * This is an INTEGRATION test:
         * - We use a real H2 in-memory database (not mocks).
         * - We use real repositories + real services.
         * - We go through the controller to simulate a realistic user use case.
         *
         * Goal: verify that meal plan data + grocery list "deltas" (bought checkmarks and extra items)
         * are correctly stored and reloaded across a simulated "restart".
         */
        try (Connection connection = DriverManager.getConnection(
                // H2 in-memory database. DB_CLOSE_DELAY=-1 keeps it alive for the duration of this JVM.
                "jdbc:h2:mem:controller_use_case;DB_CLOSE_DELAY=-1", "sa", "")) {

            // 1) Arrange: initialize database schema (tables, relations, etc.)
            H2Schema.initialize(connection);

            // 2) Arrange: use a test StorageFactory so we can inject the same Connection.
            //    This avoids using a Singleton / file-based storage in tests.
            StorageFactory factory = new TestStorageFactory(connection);

            // 3) Arrange: create the grocery list service with:
            //    - Metric strategy (combines g->kg, ml->l, etc.)
            //    - GroceryDeltaRepository (persists "bought" and "extra items")
            GroceryListService groceryService =
                    new GroceryListService(new MetricGroceryListStrategy(), factory.getGroceryDeltaRepository());

            // 4) Arrange: create the controller (controller + model integration)
            MealPlannerController controller = new MealPlannerController(factory, groceryService);

            // 5) Arrange: add two recipes via the controller.
            //    This should persist them via RecipeRepository (integration with DB).
            Recipe r1 = controller.addRecipe(
                    "Pasta dish",
                    "Simple pasta",
                    List.of("Dinner"),
                    List.of(
                            // Same ingredient name as in recipe 2 => should be summed in grocery list.
                            new Ingredient("Pasta", 100, "g"),

                            // Different ingredient => should appear separately in grocery list.
                            // (Changed to ml to keep it realistic; if you intended kg, you can revert.)
                            new Ingredient("Tomato sauce", 200, "ml")));

            Recipe r2 = controller.addRecipe(
                    "Cheesy pasta",
                    "More pasta",
                    List.of(),
                    List.of(
                            // Same ingredient name => should be summed with Pasta from recipe 1.
                            new Ingredient("Pasta", 150, "g"),
                            new Ingredient("Cheese", 50, "g")));

            // 6) Arrange: create a weekly meal plan and store/select it through the controller.
            //    The controller should:
            //    - persist the plan via MealPlanRepository
            //    - select it as the current plan, which triggers grocery list generation
            LocalDate date = LocalDate.of(2025, 1, 1);
            Map<MealType, List<Recipe>> meals = new HashMap<>();
            meals.put(MealType.DINNER, List.of(r1, r2));
            controller.addMealPlan(new MealPlan(date, meals));

            // 7) Assert: grocery list should be automatically generated from the meal plan.
            //    This checks the controller -> service -> strategy pipeline.
            List<GroceryItem> list = controller.getGroceryList();

            // Find the combined "Pasta" entry
            GroceryItem pasta = list.stream()
                    .filter(i -> i.ingredient().name().equals("Pasta"))
                    .findFirst()
                    .orElseThrow();

            // Pasta should be 100g + 150g = 250g
            assertEquals(250.0, pasta.ingredient().amount());
            assertEquals("g", pasta.ingredient().unit());

            // Tomato sauce and Cheese should also be present
            assertTrue(list.stream().anyMatch(i -> i.ingredient().name().equals("Tomato sauce")));
            assertTrue(list.stream().anyMatch(i -> i.ingredient().name().equals("Cheese")));

            // 8) Act: user checks off an item as bought.
            //    This should be stored as a "delta" for the current week.
            controller.setGroceryItemBought("Pasta", "g", true);

            // 9) Assert: the bought state is reflected in the grocery list immediately.
            GroceryItem pastaAfterBought = controller.getGroceryList().stream()
                    .filter(i -> i.ingredient().name().equals("Pasta"))
                    .findFirst()
                    .orElseThrow();
            assertTrue(pastaAfterBought.bought());

            // 10) Act: user adds an extra item manually.
            //     This should also be stored as a grocery delta for the current week.
            controller.addExtraGroceryItem(new Ingredient("Soap", 1, "oz"));

            // 11) Assert: extra item appears in the grocery list.
            assertTrue(controller.getGroceryList().stream().anyMatch(i -> i.ingredient().name().equals("Soap")));

            // 12) Simulate an "application restart":
            //     - Create a new GroceryListService instance
            //     - Create a new Controller instance
            //     - Keep the same database (same connection) so persisted data remains available
            GroceryListService groceryService2 =
                    new GroceryListService(new MetricGroceryListStrategy(), factory.getGroceryDeltaRepository());
            MealPlannerController controller2 = new MealPlannerController(factory, groceryService2);

            // 13) After restart: load the meal plan from persistence and select it.
            //     Selecting should trigger grocery list recomputation AND delta reloading for that week.
            MealPlan loadedPlan = controller2.getMealPlan(date).orElseThrow();
            controller2.selectMealPlan(loadedPlan);

            // 14) Assert: grocery deltas were reloaded from the DB:
            //     - Extra item "Soap" should still be there
            //     - "Pasta" should still be checked off as bought
            List<GroceryItem> listAfterRestart = controller2.getGroceryList();

            assertTrue(listAfterRestart.stream().anyMatch(i -> i.ingredient().name().equals("Soap")));
            assertTrue(listAfterRestart.stream()
                    .anyMatch(i -> i.ingredient().name().equals("Pasta") && i.bought()));
        }
    }

    /**
     * Test-specific StorageFactory:
     *
     * We want a real DB-backed integration test, but we also want full control
     * over the Connection and avoid using a global Singleton factory.
     *
     * This factory returns H2 repositories that all share the same Connection.
     */
    private static final class TestStorageFactory extends StorageFactory {
        private final Connection connection;

        // Lazy-initialized repositories (created only when first requested)
        private RecipeRepository recipeRepository;
        private MealPlanRepository mealPlanRepository;
        private GroceryDeltaRepository groceryDeltaRepository;

        private TestStorageFactory(Connection connection) {
            this.connection = connection;
        }

        @Override
        public RecipeRepository getRecipeRepository() {
            // Create once, reuse afterwards
            if (recipeRepository == null) {
                recipeRepository = new H2RecipeRepository(connection);
            }
            return recipeRepository;
        }

        @Override
        public MealPlanRepository getMealPlanRepository() {
            // Create once, reuse afterwards
            if (mealPlanRepository == null) {
                // H2MealPlanRepository needs RecipeRepository to resolve recipe relations
                mealPlanRepository = new H2MealPlanRepository(connection, getRecipeRepository());
            }
            return mealPlanRepository;
        }

        @Override
        public GroceryDeltaRepository getGroceryDeltaRepository() {
            // Create once, reuse afterwards
            if (groceryDeltaRepository == null) {
                groceryDeltaRepository = new H2GroceryDeltaRepository(connection);
            }
            return groceryDeltaRepository;
        }
    }
}
