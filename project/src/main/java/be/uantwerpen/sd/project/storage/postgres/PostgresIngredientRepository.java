package be.uantwerpen.sd.project.storage.postgres;

import be.uantwerpen.sd.project.storage.IngredientRepository;

import be.uantwerpen.sd.project.data.Ingredient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresIngredientRepository implements IngredientRepository {
    @Override
    public Ingredient save(Ingredient ingredient) {
        System.out.println("[Postgres] Saved ingredient: " + ingredient.name());
        return ingredient;
    }

    @Override
    public Optional<Ingredient> findByName(String name) {
        System.out.println("[Postgres] Finding ingredient by name: " + name);
        return Optional.empty();
    }

    @Override
    public List<Ingredient> findAll() {
        System.out.println("[Postgres] Finding all ingredients");
        return new ArrayList<>();
    }
}
