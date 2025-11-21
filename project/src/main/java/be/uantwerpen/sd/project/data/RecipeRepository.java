package be.uantwerpen.sd.project.data;

import be.uantwerpen.sd.project.model.Recipe;
import java.util.List;

public interface RecipeRepository {
    void save(Recipe recipe);

    List<Recipe> findAll();
}
