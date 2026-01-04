package be.uantwerpen.sd.project.model.storage;

import be.uantwerpen.sd.project.model.Recipe;
import java.util.List;
import java.util.Optional;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    void deleteById(long id);

    Optional<Recipe> findById(long id);

    List<Recipe> findAll();
}
