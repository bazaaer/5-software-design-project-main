package be.uantwerpen.sd.project.storage.postgres;

import be.uantwerpen.sd.project.storage.IngredientRepository;
import be.uantwerpen.sd.project.storage.MealPlanRepository;
import be.uantwerpen.sd.project.storage.RecipeRepository;
import be.uantwerpen.sd.project.storage.StorageFactory;

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
    public IngredientRepository getIngredientRepository() {
        return new PostgresIngredientRepository();
    }

    @Override
    public MealPlanRepository getMealPlanRepository() {
        return new PostgresMealPlanRepository();
    }
}
