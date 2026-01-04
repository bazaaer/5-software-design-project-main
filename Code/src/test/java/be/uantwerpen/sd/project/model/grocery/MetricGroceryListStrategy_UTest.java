package be.uantwerpen.sd.project.model.grocery;
import be.uantwerpen.sd.project.model.Ingredient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class MetricGroceryListStrategy_UTest {
    @Test
    public void combine_testRoundDown() {

        // Arrange all the test variables
        MetricGroceryListStrategy strategy = new MetricGroceryListStrategy();
        List<Ingredient> inputValues = List.of(
                // Combination just below 1000 g
                new Ingredient("Pasta", 499, "g"),
                new Ingredient("Pasta", 500, "g")
                );

        // Execute the combine method
        List<Ingredient> result = strategy.combine(inputValues);

        // Compare the input with the output
        assertEquals(1, result.size());

        Ingredient ingredient = result.getFirst();
        assertEquals("Pasta", ingredient.name());
        assertEquals(999, ingredient.amount());
        assertEquals("g", ingredient.unit());

    }

    @Test
    public void combine_TestConvertUnit() {

        // Arrange all the test variables
        MetricGroceryListStrategy strategy = new MetricGroceryListStrategy();

        List<Ingredient> inputValues = List.of(
                // Combination exact 1000 g
                new Ingredient("Tomato Sauce", 1000,  "g")
        );

        // Execute the combine method
        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(1, result.size());

        Ingredient ingredient = result.getFirst();
        assertEquals("Tomato Sauce", ingredient.name());
        assertEquals(1, ingredient.amount());
        assertEquals("kg", ingredient.unit());
    }


    @Test
    public void combine_TestRoundUp() {
        MetricGroceryListStrategy strategy = new MetricGroceryListStrategy();
        List<Ingredient> inputValues = List.of(
                // Combination just above 1000 g
                new Ingredient("Meat", 501, "g"),
                new Ingredient("Meat", 500, "g")
        );

        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(1, result.size());

        Ingredient ingredient = result.getFirst();
        assertEquals("Meat", ingredient.name());
        assertEquals(1.001, ingredient.amount());
        assertEquals("kg", ingredient.unit());
    }

    @Test
    public void combine_TestMixedUnits() {
        MetricGroceryListStrategy strategy = new MetricGroceryListStrategy();
        List<Ingredient> inputValues = List.of(
                new Ingredient("Tomato Sauce", 500,  "g"),
                new Ingredient("Tomato Sauce", 500, "ml"),
                new Ingredient("Meat", 500, "g")
        );

        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(3, result.size());

        // Find and check Tomato Sauce with unit = g
        Ingredient ingredient1 = result.stream()
                .filter(ingredient -> ingredient.name().equals("Tomato Sauce"))
                .filter(ingredient -> ingredient.unit().equals("g"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingredient does not return desired unit [g]"));

        // Find and check Tomato Sauce with unit = ml
        Ingredient ingredient2 = result.stream()
                .filter(ingredient -> ingredient.name().equals("Tomato Sauce"))
                .filter(ingredient -> ingredient.unit().equals("ml"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingredient does not return desired unit [ml]"));

        // Find and check Meat
        Ingredient ingredient3 = result.stream()
                .filter(ingredient -> ingredient.name().equals("Meat"))
                .filter(ingredient -> ingredient.unit().equals("g"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingredient does not return desired unit [g]"));

        // Check values for each ingredient
        assertEquals(500, ingredient1.amount());
        assertEquals(500, ingredient2.amount());
        assertEquals(500, ingredient3.amount());
    }
}
