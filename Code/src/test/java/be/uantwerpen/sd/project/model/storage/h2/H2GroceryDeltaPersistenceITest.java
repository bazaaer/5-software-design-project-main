package be.uantwerpen.sd.project.model.storage.h2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.grocery.GroceryListService;
import be.uantwerpen.sd.project.model.grocery.MetricGroceryListStrategy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class H2GroceryDeltaPersistenceITest {

    @Test
    void checkmarksAndExtraItemsReloadForSameWeek() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:grocery_delta;DB_CLOSE_DELAY=-1", "sa", "")) {
            H2Schema.initialize(connection);
            H2GroceryDeltaRepository repo = new H2GroceryDeltaRepository(connection);

            LocalDate wednesday = LocalDate.of(2025, 1, 1);
            MealPlan plan = new MealPlan(wednesday, Map.of(
                    MealType.DINNER,
                    List.of(new Recipe("R", "", List.of(), List.of(new Ingredient("Pasta", 500, "g"))))));

            GroceryListService serviceA = new GroceryListService(new MetricGroceryListStrategy(), repo);
            serviceA.setMealPlan(plan);
            serviceA.addExtraItem(new Ingredient("Soap", 1, "oz"));
            serviceA.setBought("Pasta", "g", true);

            GroceryListService serviceB = new GroceryListService(new MetricGroceryListStrategy(), repo);
            serviceB.setMealPlan(plan);

            assertEquals(2, serviceB.getGroceryList().size());
            assertTrue(serviceB.getGroceryList().stream().anyMatch(i -> i.ingredient().name().equals("Soap")));
            assertTrue(serviceB.getGroceryList().stream()
                    .anyMatch(i -> i.ingredient().name().equals("Pasta") && i.bought()));
        }
    }
}

