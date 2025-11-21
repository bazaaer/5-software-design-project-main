package be.uantwerpen.sd.project.data;

import be.uantwerpen.sd.project.model.Recipe;
import java.util.ArrayList;
import java.util.List;

public class PostgresRecipeRepository implements RecipeRepository {
    @Override
    public void save(Recipe recipe) {
        System.out.println("[Postgres] Saving recipe: " + recipe.getTitle());
        // Real Postgres JDBC code would go here
    }

    @Override
    public List<Recipe> findAll() {
        System.out.println("[Postgres] Finding all recipes");
        return new ArrayList<>();
    }
}
