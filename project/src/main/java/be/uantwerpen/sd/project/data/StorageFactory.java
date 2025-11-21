package be.uantwerpen.sd.project.data;

import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class StorageFactory {

    // The "Smart Switch" (Singleton/Factory Method)
    private static StorageFactory instance;

    public static StorageFactory getInstance() {
        if (instance == null) {
            if (isPostgresAvailable()) {
                instance = new PostgresStorageFactory();
            } else {
                instance = new H2StorageFactory();
            }
        }
        return instance;
    }

    // Helper to check if Postgres is running
    private static boolean isPostgresAvailable() {
        try {
            // Try to connect with a short timeout
            DriverManager.getConnection("jdbc:postgresql://localhost:5432/mealplanner", "student", "password");
            System.out.println("StorageFactory: Postgres detected!");
            return true;
        } catch (SQLException e) {
            System.out.println("StorageFactory: Postgres not found, falling back to H2.");
            return false;
        }
    }

    // The Abstract Method (What we produce)
    public abstract RecipeRepository getRecipeRepository();
}
