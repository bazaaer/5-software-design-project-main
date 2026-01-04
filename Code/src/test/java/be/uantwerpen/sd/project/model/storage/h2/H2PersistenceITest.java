package be.uantwerpen.sd.project.model.storage.h2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class H2PersistenceITest {

    @Test
    void mealPlanLoadsRecipesWithTagsAndIngredients() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "")) {
            H2Schema.initialize(connection);
            RecipeRepository recipeRepository = new H2RecipeRepository(connection);
            H2MealPlanRepository mealPlanRepository = new H2MealPlanRepository(connection, recipeRepository);

            Recipe savedRecipe = recipeRepository.save(new Recipe(
                    "Spaghetti",
                    "Classic pasta",
                    List.of("Dinner", "Italian"),
                    List.of(
                            new Ingredient("Pasta", 100, "g"),
                            new Ingredient("Tomato sauce", 1, "jar"))));

            Map<MealType, List<Recipe>> meals = new HashMap<>();
            meals.put(MealType.DINNER, List.of(savedRecipe));

            LocalDate date = LocalDate.of(2025, 1, 1);
            mealPlanRepository.save(new MealPlan(date, meals));

            MealPlan loaded = mealPlanRepository.findByDate(date).orElseThrow();
            Recipe loadedRecipe = loaded.meals().get(MealType.DINNER).getFirst();

            assertNotNull(loadedRecipe.id());
            assertEquals(List.of("Dinner", "Italian"), loadedRecipe.tags());
            assertEquals(2, loadedRecipe.ingredients().size());
            assertTrue(loadedRecipe.ingredients().stream().allMatch(i -> i.name() != null && !i.name().isBlank()));
        }
    }

    @Test
    void recipeIngredientsStaySeparatePerRecipe() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1", "sa", "")) {
            H2Schema.initialize(connection);
            RecipeRepository recipeRepository = new H2RecipeRepository(connection);

            Recipe a = recipeRepository.save(new Recipe(
                    "Recipe A",
                    "A",
                    List.of(),
                    List.of(new Ingredient("Pasta", 100, "g"))));
            Recipe b = recipeRepository.save(new Recipe(
                    "Recipe B",
                    "B",
                    List.of(),
                    List.of(new Ingredient("Pasta", 200, "g"))));

            Recipe loadedA = recipeRepository.findById(a.id()).orElseThrow();
            Recipe loadedB = recipeRepository.findById(b.id()).orElseThrow();

            assertEquals(100, loadedA.ingredients().getFirst().amount());
            assertEquals(200, loadedB.ingredients().getFirst().amount());
        }
    }

    @Test
    void recipeCanBeUpdatedAndDeleted() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test3;DB_CLOSE_DELAY=-1", "sa", "")) {
            H2Schema.initialize(connection);
            RecipeRepository recipeRepository = new H2RecipeRepository(connection);
            H2MealPlanRepository mealPlanRepository = new H2MealPlanRepository(connection, recipeRepository);

            Recipe created = recipeRepository.save(new Recipe(
                    "Old title",
                    "Old description",
                    List.of("old"),
                    List.of(new Ingredient("Pasta", 100, "g"))));

            Recipe updated = recipeRepository.save(new Recipe(
                    created.id(),
                    "New title",
                    "New description",
                    List.of("new", "quick"),
                    List.of(new Ingredient("Pasta", 200, "g"))));

            Recipe reloaded = recipeRepository.findById(updated.id()).orElseThrow();
            assertEquals("New title", reloaded.title());
            assertEquals("New description", reloaded.description());
            assertEquals(List.of("new", "quick"), reloaded.tags());
            assertEquals(200, reloaded.ingredients().getFirst().amount());

            LocalDate date = LocalDate.of(2025, 2, 1);
            mealPlanRepository.save(new MealPlan(date, Map.of(MealType.DINNER, List.of(updated))));

            recipeRepository.deleteById(updated.id());

            assertEquals(true, recipeRepository.findById(updated.id()).isEmpty());
            MealPlan loadedPlan = mealPlanRepository.findByDate(date).orElseThrow();
            assertEquals(true, loadedPlan.meals().getOrDefault(MealType.DINNER, List.of()).isEmpty());
        }
    }
}
