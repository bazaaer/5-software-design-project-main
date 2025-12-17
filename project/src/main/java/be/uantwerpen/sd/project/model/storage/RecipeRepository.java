package be.uantwerpen.sd.project.model.storage;

import be.uantwerpen.sd.project.model.Recipe;
import java.util.List;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    List<Recipe> findAll();
}
