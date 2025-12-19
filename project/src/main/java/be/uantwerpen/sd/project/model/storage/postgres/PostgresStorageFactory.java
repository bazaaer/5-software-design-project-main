package be.uantwerpen.sd.project.model.storage.postgres;

import be.uantwerpen.sd.project.model.storage.MealPlanRepository;
import be.uantwerpen.sd.project.model.storage.GroceryDeltaRepository;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.storage.StorageFactory;

public class PostgresStorageFactory extends StorageFactory {
    private static PostgresStorageFactory instance;

    // Singleton Pattern Implemented Here
    private PostgresStorageFactory() {
        // Initialize Postgres connection (placeholder)
    }

    public static synchronized PostgresStorageFactory getInstance() {
        if (instance == null) {
            instance = new PostgresStorageFactory();
        }
        return instance;
    }

    @Override
    public RecipeRepository getRecipeRepository() {
        return new PostgresRecipeRepository();
    }

    @Override
    public MealPlanRepository getMealPlanRepository() {
        return new PostgresMealPlanRepository();
    }

    @Override
    public GroceryDeltaRepository getGroceryDeltaRepository() {
        throw new UnsupportedOperationException("Postgres storage not implemented yet.");
    }
}
