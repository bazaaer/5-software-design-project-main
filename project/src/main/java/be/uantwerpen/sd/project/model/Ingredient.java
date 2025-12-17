package be.uantwerpen.sd.project.model;

public record Ingredient(Long id, String name, double amount, String unit) {
    public Ingredient(String name, double amount, String unit) {
        this(null, name, amount, unit);
    }
}
