package be.uantwerpen.sd.project.model.storage;

import be.uantwerpen.sd.project.model.storage.h2.H2StorageFactory;
import be.uantwerpen.sd.project.model.storage.postgres.PostgresStorageFactory;
import io.github.cdimascio.dotenv.Dotenv;

public abstract class StorageFactory {

    private static volatile StorageFactory instance;
    private static final Dotenv DOTENV = Dotenv.load();

    // Abstract Factory entry point and thread-safe singleton
    public static StorageFactory getInstance() {
        if (instance == null) {
            synchronized (StorageFactory.class) {
                if (instance == null) {
                    String dbType = DOTENV.get("DB_TYPE");
                    if ("postgres".equals(dbType)) {
                        instance = PostgresStorageFactory.getInstance();
                    } else {
                        instance = H2StorageFactory.getInstance();
                    }
                }
            }
        }
        return instance;
    }

    public abstract RecipeRepository getRecipeRepository();

    public abstract MealPlanRepository getMealPlanRepository();

    public abstract GroceryDeltaRepository getGroceryDeltaRepository();
}
