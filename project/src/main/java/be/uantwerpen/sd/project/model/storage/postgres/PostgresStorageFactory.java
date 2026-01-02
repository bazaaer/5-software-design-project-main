package be.uantwerpen.sd.project.model.storage.postgres;

import be.uantwerpen.sd.project.model.storage.MealPlanRepository;
import be.uantwerpen.sd.project.model.storage.GroceryDeltaRepository;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresStorageFactory extends StorageFactory {
    private static volatile PostgresStorageFactory instance;
    private static final Dotenv DOTENV = Dotenv.load();
    private final Connection connection;
    private RecipeRepository recipeRepository;
    private MealPlanRepository mealPlanRepository;
    private GroceryDeltaRepository groceryDeltaRepository;

    // Abstract Factory: Postgres concrete factory
    private PostgresStorageFactory() {
        try {
            String host = DOTENV.get("POSTGRES_HOST");
            String port = DOTENV.get("POSTGRES_PORT");
            String database = DOTENV.get("POSTGRES_DB");
            String user = DOTENV.get("POSTGRES_USER");
            String password = DOTENV.get("POSTGRES_PASSWORD");
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            this.connection = DriverManager.getConnection(url, user, password);
            PostgresSchema.initialize(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PostgresStorageFactory getInstance() {
        // thread-safe singleton (double check)
        if (instance == null) {
            synchronized (PostgresStorageFactory.class) {
                if (instance == null) {
                    instance = new PostgresStorageFactory();
                }
            }
        }
        return instance;
    }

    @Override
    public RecipeRepository getRecipeRepository() {
        if (recipeRepository == null) {
            recipeRepository = new PostgresRecipeRepository(connection);
        }
        return recipeRepository;
    }

    @Override
    public MealPlanRepository getMealPlanRepository() {
        if (mealPlanRepository == null) {
            mealPlanRepository = new PostgresMealPlanRepository(connection, getRecipeRepository());
        }
        return mealPlanRepository;
    }

    @Override
    public GroceryDeltaRepository getGroceryDeltaRepository() {
        if (groceryDeltaRepository == null) {
            groceryDeltaRepository = new PostgresGroceryDeltaRepository(connection);
        }
        return groceryDeltaRepository;
    }
}
