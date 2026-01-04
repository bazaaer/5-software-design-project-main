package be.uantwerpen.sd.project.model;

import java.util.List;

public record Recipe(Long id, String title, String description, List<String> tags, List<Ingredient> ingredients) {
    public Recipe(String title, String description, List<String> tags, List<Ingredient> ingredients) {
        this(null, title, description, tags, ingredients);
    }
}
