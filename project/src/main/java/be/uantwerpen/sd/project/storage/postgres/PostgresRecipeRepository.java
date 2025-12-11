package be.uantwerpen.sd.project.storage.postgres;

import be.uantwerpen.sd.project.storage.RecipeRepository;

import be.uantwerpen.sd.project.data.Recipe;
import java.util.ArrayList;
import java.util.List;

public class PostgresRecipeRepository implements RecipeRepository {
    @Override
    public Recipe save(Recipe recipe) {
        System.out.println("[Postgres] Saved recipe: " + recipe.title());
        return recipe;
    }
    // TODO: add the actual logic

    @Override
    public List<Recipe> findAll() {
        System.out.println("[Postgres] Finding all recipes");
        return new ArrayList<>();
    }
}
