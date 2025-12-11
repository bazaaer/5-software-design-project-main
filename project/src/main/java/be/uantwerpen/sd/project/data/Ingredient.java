package be.uantwerpen.sd.project.data;

public record Ingredient(Long id, String name, double amount, String unit) {
    public Ingredient(String name, double amount, String unit) {
        this(null, name, amount, unit);
    }
}
