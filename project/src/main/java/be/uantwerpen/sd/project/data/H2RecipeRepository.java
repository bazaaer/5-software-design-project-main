package be.uantwerpen.sd.project.data;

import be.uantwerpen.sd.project.model.Recipe;
import java.util.ArrayList;
import java.util.List;

public class H2RecipeRepository implements RecipeRepository {
    @Override
    public void save(Recipe recipe) {
        System.out.println("[H2] Saving recipe: " + recipe.getTitle());
        // TODO: add the code for actually saving the recepe
    }

    @Override
    public List<Recipe> findAll() {
        System.out.println("[H2] Finding all recipes");
        return new ArrayList<>();
    }
}
