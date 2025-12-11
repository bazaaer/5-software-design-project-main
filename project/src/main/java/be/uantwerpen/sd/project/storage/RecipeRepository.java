package be.uantwerpen.sd.project.storage;

import be.uantwerpen.sd.project.data.Recipe;
import java.util.List;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    List<Recipe> findAll();
}
