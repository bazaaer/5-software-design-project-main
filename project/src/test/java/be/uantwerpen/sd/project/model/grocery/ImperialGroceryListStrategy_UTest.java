package be.uantwerpen.sd.project.model.grocery;
import be.uantwerpen.sd.project.model.Ingredient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ImperialGroceryListStrategy_UTest {
    @Test
    public void combine_testUnderOZ() {

        // Arrange all the test variables
        ImperialGroceryListStrategy strategy = new ImperialGroceryListStrategy();
        List<Ingredient> inputValues = List.of(
                // Input just below 1000 g
                new Ingredient("Pasta", 28, "g")
        );

        // Execute the combine method
        List<Ingredient> result = strategy.combine(inputValues);

        // Compare the input with the output
        assertEquals(1, result.size());

        Ingredient ingredient = result.get(0);
        assertEquals("Pasta", ingredient.name());
        assertEquals(0.988, ingredient.amount());
        assertEquals("oz", ingredient.unit());

    }

    @Test
    public void combine_testOnOZ() {

        // Arrange all the test variables
        ImperialGroceryListStrategy strategy = new ImperialGroceryListStrategy();

        List<Ingredient> inputValues = List.of(
                // Input exactly 1 Oz
                new Ingredient("Pasta", 28.3495, "g")
        );

        // Execute the combine method
        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(1, result.size());

        Ingredient ingredient = result.get(0);
        assertEquals("Pasta", ingredient.name());
        assertEquals(1, ingredient.amount());
        assertEquals("oz", ingredient.unit());
    }

    @Test
    public void combine_testAboveOZ() {

        // Arrange all the test variables
        ImperialGroceryListStrategy strategy = new ImperialGroceryListStrategy();

        List<Ingredient> inputValues = List.of(
                // Input just above 1 OZ
                new Ingredient("Pasta", 29, "g")
        );

        // Execute the combine method
        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(1, result.size());

        Ingredient ingredient = result.get(0);
        assertEquals("Pasta", ingredient.name());
        assertEquals(1.023, ingredient.amount());
        assertEquals("oz", ingredient.unit());
    }


    @Test
    public void combine_TestChangeUnitUpwards() {
        ImperialGroceryListStrategy strategy = new ImperialGroceryListStrategy();

        List<Ingredient> inputValues = List.of(
                // Combination just under 1 LB
                new Ingredient("Meat", 225, "g"),
                new Ingredient("Meat", 228.5, "g")
        );

        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(1, result.size());

        Ingredient ingredient = result.get(0);
        assertEquals("Meat", ingredient.name());
        assertEquals(15.997, ingredient.amount());
        assertEquals("oz", ingredient.unit());
    }

    @Test
    public void combine_TestChangeUnitOn() {
        ImperialGroceryListStrategy strategy = new ImperialGroceryListStrategy();

        List<Ingredient> inputValues = List.of(
                // Combination just under 1 LB
                new Ingredient("Meat", 225, "g"),
                new Ingredient("Meat", 228.59237, "g")
        );

        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(1, result.size());

        Ingredient ingredient = result.get(0);
        assertEquals("Meat", ingredient.name());
        assertEquals(16, ingredient.amount());
        assertEquals("oz", ingredient.unit());
    }

    @Test
    public void combine_TestChangeUnitAbove() {
        ImperialGroceryListStrategy strategy = new ImperialGroceryListStrategy();

        List<Ingredient> inputValues = List.of(
                // Combination just under 1 LB
                new Ingredient("Meat", 225, "g"),
                new Ingredient("Meat", 229, "g")
        );

        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(1, result.size());

        Ingredient ingredient = result.get(0);
        assertEquals("Meat", ingredient.name());
        assertEquals(1.001, ingredient.amount());
        assertEquals("lb", ingredient.unit());
    }


    @Test
    public void combine_TestMixedUnits() {
        ImperialGroceryListStrategy strategy = new ImperialGroceryListStrategy();

        List<Ingredient> inputValues = List.of(
                new Ingredient("Tomato Sauce", 453,  "g"),
                new Ingredient("Tomato Sauce", 453, "ml"),
                new Ingredient("Meat", 453, "g")
        );

        List<Ingredient> result = strategy.combine(inputValues);

        assertEquals(3, result.size());

        // Find and check Tomato Sauce with unit = g
        Ingredient ingredient1 = result.stream()
                .filter(ingredient -> ingredient.name().equals("Tomato Sauce"))
                .filter(ingredient -> ingredient.unit().equals("oz"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingredient does not return desired unit [oz]"));

        // Find and check Tomato Sauce with unit = ml
        Ingredient ingredient2 = result.stream()
                .filter(ingredient -> ingredient.name().equals("Tomato Sauce"))
                .filter(ingredient -> ingredient.unit().equals("fl oz"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingredient does not return desired unit [fl oz]"));

        // Find and check Meat
        Ingredient ingredient3 = result.stream()
                .filter(ingredient -> ingredient.name().equals("Meat"))
                .filter(ingredient -> ingredient.unit().equals("oz"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Ingredient does not return desired unit [oz]"));

        // Check values for each ingredient
        assertEquals(15.979, ingredient1.amount());
        assertEquals(15.318, ingredient2.amount());
        assertEquals(15.979, ingredient3.amount());
    }
}
