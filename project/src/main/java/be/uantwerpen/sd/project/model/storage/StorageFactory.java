package be.uantwerpen.sd.project.model.storage;

import java.sql.DriverManager;
import java.sql.SQLException;
import be.uantwerpen.sd.project.model.storage.h2.H2StorageFactory;
import be.uantwerpen.sd.project.model.storage.postgres.PostgresStorageFactory;

public abstract class StorageFactory {

    // The "Smart Switch" (Singleton/Factory Method)
    private static StorageFactory instance;

    public static StorageFactory getInstance() {
        if (instance == null) {
            if (isPostgresAvailable()) {
                instance = PostgresStorageFactory.getInstance();
            } else {
                instance = H2StorageFactory.getInstance();
            }
        }
        return instance;
    }

    // Helper to check if Postgres is running
    private static boolean isPostgresAvailable() {
        try {
            // Try to connect with a short timeout
            DriverManager.getConnection("jdbc:postgresql://localhost:5432/mealplanner", "student", "password");
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // The Abstract Method (What we produce)
    public abstract RecipeRepository getRecipeRepository();

    public abstract IngredientRepository getIngredientRepository();

    public abstract MealPlanRepository getMealPlanRepository();
}
