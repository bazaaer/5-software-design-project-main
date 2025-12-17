package be.uantwerpen.sd.project.model.storage;

import be.uantwerpen.sd.project.model.Ingredient;
import java.util.List;
import java.util.Optional;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findByName(String name);

    List<Ingredient> findAll();
}
