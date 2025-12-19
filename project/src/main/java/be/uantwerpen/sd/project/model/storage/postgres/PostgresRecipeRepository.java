package be.uantwerpen.sd.project.model.storage.postgres;

import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.Recipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresRecipeRepository implements RecipeRepository {
    @Override
    public Recipe save(Recipe recipe) {
        System.out.println("[Postgres] Saved recipe: " + recipe.title());
        return recipe;
    }
    // TODO: add the actual logic

    @Override
    public void deleteById(long id) {
        System.out.println("[Postgres] Deleting recipe by id: " + id);
    }

    @Override
    public Optional<Recipe> findById(long id) {
        System.out.println("[Postgres] Finding recipe by id: " + id);
        return Optional.empty();
    }

    @Override
    public List<Recipe> findAll() {
        System.out.println("[Postgres] Finding all recipes");
        return new ArrayList<>();
    }
}
