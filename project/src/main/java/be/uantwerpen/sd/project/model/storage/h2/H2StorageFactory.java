package be.uantwerpen.sd.project.model.storage.h2;

import be.uantwerpen.sd.project.model.storage.MealPlanRepository;
import be.uantwerpen.sd.project.model.storage.GroceryDeltaRepository;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2StorageFactory extends StorageFactory {
    private final Connection connection;
    private static volatile H2StorageFactory instance;
    private static final Dotenv DOTENV = Dotenv.load();
    private RecipeRepository recipeRepository;
    private MealPlanRepository mealPlanRepository;
    private GroceryDeltaRepository groceryDeltaRepository;

    // Abstract Factory: H2 concrete factory
    // thread-safe singleton so this only gets built once
    private H2StorageFactory() {
        try {
            String url = DOTENV.get("H2_URL");
            String user = DOTENV.get("H2_USER");
            String password = DOTENV.get("H2_PASSWORD");
            this.connection = DriverManager.getConnection(url, user, password);
            H2Schema.initialize(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static H2StorageFactory getInstance() {
        // thread-safe singleton (double check)
        if (instance == null) {
            synchronized (H2StorageFactory.class) {
                if (instance == null) {
                    instance = new H2StorageFactory();
                }
            }
        }
        return instance;
    }

    @Override
    public RecipeRepository getRecipeRepository() {
        if (recipeRepository == null) {
            recipeRepository = new H2RecipeRepository(connection);
        }
        return recipeRepository;
    }

    @Override
    public MealPlanRepository getMealPlanRepository() {
        if (mealPlanRepository == null) {
            mealPlanRepository = new H2MealPlanRepository(connection, getRecipeRepository());
        }
        return mealPlanRepository;
    }

    @Override
    public GroceryDeltaRepository getGroceryDeltaRepository() {
        if (groceryDeltaRepository == null) {
            groceryDeltaRepository = new H2GroceryDeltaRepository(connection);
        }
        return groceryDeltaRepository;
    }
}
