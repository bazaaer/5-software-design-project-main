package be.uantwerpen.sd.project.model.grocery;

enum Unit {
    G("g", Dimension.WEIGHT, 1.0),
    KG("kg", Dimension.WEIGHT, 1000.0),
    OZ("oz", Dimension.WEIGHT, 28.349523125),
    LB("lb", Dimension.WEIGHT, 453.59237),

    ML("ml", Dimension.VOLUME, 1.0),
    L("l", Dimension.VOLUME, 1000.0),
    FL_OZ("fl oz", Dimension.VOLUME, 29.5735295625);

    private final String symbol;
    private final Dimension dimension;
    private final double toMetricBaseFactor;

    Unit(String symbol, Dimension dimension, double toMetricBaseFactor) {
        this.symbol = symbol;
        this.dimension = dimension;
        this.toMetricBaseFactor = toMetricBaseFactor;
    }

    Dimension dimension() {
        return dimension;
    }

    double toMetricBase(double amount) {
        return amount * toMetricBaseFactor;
    }

    double fromMetricBase(double metricBaseAmount) {
        return metricBaseAmount / toMetricBaseFactor;
    }

    String symbol() {
        return symbol;
    }

    static Unit parse(String unit) {
        String normalized = normalizeUnit(unit);
        return switch (normalized) {
            case "g" -> G;
            case "kg" -> KG;
            case "oz" -> OZ;
            case "lb" -> LB;
            case "ml" -> ML;
            case "l" -> L;
            case "fl oz" -> FL_OZ;
            default -> throw new IllegalArgumentException("Unsupported unit: " + unit);
        };
    }

    private static String normalizeUnit(String unit) {
        if (unit == null) {
            return "";
        }
        return unit.trim().toLowerCase().replace('_', ' ').replaceAll("\\s+", " ");
    }
}

