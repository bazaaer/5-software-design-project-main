package be.uantwerpen.sd.project.model.grocery;

import be.uantwerpen.sd.project.model.Ingredient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricGroceryListStrategy implements GroceryListStrategy {

    @Override
    public List<Ingredient> combine(List<Ingredient> ingredients) {
        // Strategy implementation: metric merge and unit conversion
        Map<Key, Accumulator> summed = new HashMap<>();

        for (Ingredient ingredient : ingredients) {
            String name = normalizeName(ingredient.name());
            Unit unit = Unit.parse(ingredient.unit());
            double metricBaseAmount = unit.toMetricBase(ingredient.amount());

            Key key = new Key(name.toLowerCase(), unit.dimension());
            summed.computeIfAbsent(key, ignored -> new Accumulator(name, unit.dimension())).metricBaseAmount += metricBaseAmount;
        }

        List<Ingredient> result = new ArrayList<>();
        for (Accumulator accumulator : summed.values()) {
            result.add(formatMetric(accumulator.displayName, accumulator.dimension, accumulator.metricBaseAmount));
        }

        result.sort(Comparator.comparing(Ingredient::name).thenComparing(Ingredient::unit));
        return result;
    }

    private static Ingredient formatMetric(String name, Dimension dimension, double metricBaseAmount) {
        if (dimension == Dimension.WEIGHT) {
            if (metricBaseAmount >= 1000.0) {
                return new Ingredient(name, round3(metricBaseAmount / 1000.0), "kg");
            }
            return new Ingredient(name, round3(metricBaseAmount), "g");
        }

        if (metricBaseAmount >= 1000.0) {
            return new Ingredient(name, round3(metricBaseAmount / 1000.0), "l");
        }
        return new Ingredient(name, round3(metricBaseAmount), "ml");
    }

    private static String normalizeName(String name) {
        return name.trim();
    }

    private static double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }

    private record Key(String nameKey, Dimension dimension) {
    }

    private static final class Accumulator {
        private final String displayName;
        private final Dimension dimension;
        private double metricBaseAmount;

        private Accumulator(String displayName, Dimension dimension) {
            this.displayName = displayName;
            this.dimension = dimension;
        }
    }
}
