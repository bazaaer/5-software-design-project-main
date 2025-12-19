package be.uantwerpen.sd.project.model.storage.h2;

import be.uantwerpen.sd.project.model.storage.MealPlanRepository;
import be.uantwerpen.sd.project.model.storage.GroceryDeltaRepository;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2StorageFactory extends StorageFactory {
    private Connection connection;
    private static H2StorageFactory instance;
    private RecipeRepository recipeRepository;
    private MealPlanRepository mealPlanRepository;
    private GroceryDeltaRepository groceryDeltaRepository;

    // Singleton Pattern Implemented Here
    private H2StorageFactory() {
        try {
            String url = "jdbc:h2:file:./data/mealplanner";
            String user = "sa";
            String password = "";
            this.connection = DriverManager.getConnection(url, user, password);
            H2Schema.initialize(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized H2StorageFactory getInstance() {
        if (instance == null) {
            instance = new H2StorageFactory();
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
