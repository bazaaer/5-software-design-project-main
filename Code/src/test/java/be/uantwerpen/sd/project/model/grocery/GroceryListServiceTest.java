package be.uantwerpen.sd.project.model.grocery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

public class GroceryListServiceTest {

    @Test
    void firesPropertyChangeWhenGroceryListChanges() {
        GroceryListService service = new GroceryListService(new MetricGroceryListStrategy());
        AtomicReference<PropertyChangeEvent> event = new AtomicReference<>();
        service.addPropertyChangeListener(evt -> {
            if (GroceryListService.PROP_GROCERY_LIST.equals(evt.getPropertyName())) {
                event.set(evt);
            }
        });

        MealPlan plan = new MealPlan(LocalDate.of(2025, 1, 1), Map.of(
                MealType.DINNER, List.of(new Recipe(
                        "Spaghetti",
                        "Classic",
                        List.of(),
                        List.of(new Ingredient("Pasta", 500, "g"))))));

        service.setMealPlan(plan);

        assertTrue(event.get() != null);
        assertEquals(1, service.getGroceryList().size());
        assertEquals("Pasta", service.getGroceryList().getFirst().ingredient().name());
    }

    @Test
    void boughtStatePersistsAcrossMealPlanChanges() {
        GroceryListService service = new GroceryListService(new MetricGroceryListStrategy());
        service.setMealPlan(new MealPlan(LocalDate.of(2025, 1, 1), Map.of(
                MealType.DINNER, List.of(new Recipe(
                        "R1",
                        "d",
                        List.of(),
                        List.of(new Ingredient("Pasta", 500, "g")))))));

        service.setBought("Pasta", "g", true);
        assertTrue(service.getGroceryList().getFirst().bought());

        service.setMealPlan(new MealPlan(LocalDate.of(2025, 1, 1), Map.of(
                MealType.DINNER, List.of(new Recipe(
                        "R2",
                        "d",
                        List.of(),
                        List.of(new Ingredient("Pasta", 1, "kg")))))));

        assertTrue(service.getGroceryList().getFirst().bought());
    }

    @Test
    void boughtStateDoesNotCarryAcrossWeeks() {
        GroceryListService service = new GroceryListService(new MetricGroceryListStrategy());

        service.setMealPlan(new MealPlan(LocalDate.of(2025, 1, 1), Map.of(
                MealType.DINNER, List.of(new Recipe(
                        "R1",
                        "d",
                        List.of(),
                        List.of(new Ingredient("Pasta", 500, "g")))))));
        service.setBought("Pasta", "g", true);
        assertTrue(service.getGroceryList().getFirst().bought());

        service.setMealPlan(new MealPlan(LocalDate.of(2025, 1, 8), Map.of(
                MealType.DINNER, List.of(new Recipe(
                        "R2",
                        "d",
                        List.of(),
                        List.of(new Ingredient("Pasta", 500, "g")))))));

        assertEquals(1, service.getGroceryList().size());
        assertEquals("Pasta", service.getGroceryList().getFirst().ingredient().name());
        assertEquals(false, service.getGroceryList().getFirst().bought());
    }

    @Test
    void manualExtraItemsAreIncluded() {
        GroceryListService service = new GroceryListService(new MetricGroceryListStrategy());
        service.setMealPlan(new MealPlan(LocalDate.of(2025, 1, 1), Map.of()));
        service.addExtraItem(new Ingredient("Soap", 1, "oz"));
        assertEquals(1, service.getGroceryList().size());
        assertEquals("Soap", service.getGroceryList().getFirst().ingredient().name());
    }
}
