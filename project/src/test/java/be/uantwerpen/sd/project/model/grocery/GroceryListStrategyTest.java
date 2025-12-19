package be.uantwerpen.sd.project.model.grocery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.uantwerpen.sd.project.model.Ingredient;
import java.util.List;
import org.junit.jupiter.api.Test;

public class GroceryListStrategyTest {

    @Test
    void metricStrategyConvertsThenSumsWeight() {
        GroceryListStrategy strategy = new MetricGroceryListStrategy();
        List<Ingredient> combined = strategy.combine(List.of(
                new Ingredient("Pasta", 500, "g"),
                new Ingredient("Pasta", 1, "kg")));

        assertEquals(1, combined.size());
        assertEquals("Pasta", combined.getFirst().name());
        assertEquals("kg", combined.getFirst().unit());
        assertEquals(1.5, combined.getFirst().amount());
    }

    @Test
    void metricStrategyConvertsThenSumsVolume() {
        GroceryListStrategy strategy = new MetricGroceryListStrategy();
        List<Ingredient> combined = strategy.combine(List.of(
                new Ingredient("Milk", 500, "ml"),
                new Ingredient("Milk", 1, "l")));

        assertEquals(1, combined.size());
        assertEquals("Milk", combined.getFirst().name());
        assertEquals("l", combined.getFirst().unit());
        assertEquals(1.5, combined.getFirst().amount());
    }

    @Test
    void imperialStrategyConvertsThenSumsWeight() {
        GroceryListStrategy strategy = new ImperialGroceryListStrategy();
        List<Ingredient> combined = strategy.combine(List.of(
                new Ingredient("Pasta", 500, "g"),
                new Ingredient("Pasta", 1, "kg")));

        assertEquals(1, combined.size());
        assertEquals("Pasta", combined.getFirst().name());
        assertEquals("lb", combined.getFirst().unit());
        assertEquals(3.307, combined.getFirst().amount());
    }

    @Test
    void sameNameDifferentDimensionDoesNotCombine() {
        GroceryListStrategy strategy = new MetricGroceryListStrategy();
        List<Ingredient> combined = strategy.combine(List.of(
                new Ingredient("Salt", 1, "g"),
                new Ingredient("Salt", 1, "ml")));

        assertEquals(2, combined.size());
    }

    @Test
    void unsupportedUnitThrows() {
        GroceryListStrategy strategy = new MetricGroceryListStrategy();
        assertThrows(IllegalArgumentException.class, () -> strategy.combine(List.of(new Ingredient("X", 1, "cup"))));
    }
}

