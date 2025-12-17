package be.uantwerpen.sd.project.model.storage.h2;

import be.uantwerpen.sd.project.model.storage.IngredientRepository;
import be.uantwerpen.sd.project.model.storage.MealPlanRepository;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2StorageFactory extends StorageFactory {
    private Connection connection;
    private static H2StorageFactory instance;

    // Singleton Pattern Implemented Here
    private H2StorageFactory() {
        try {
            String url = "jdbc:h2:file:./data/mealplanner";
            String user = "sa";
            String password = "";
            this.connection = DriverManager.getConnection(url, user, password);
            initializeDatabase();
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

    private void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create Ingredients table
            stmt.execute("CREATE TABLE IF NOT EXISTS ingredients (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "amount DOUBLE, " +
                    "unit VARCHAR(50))");

            // Create Recipes table
            stmt.execute("CREATE TABLE IF NOT EXISTS recipes (" +
                    "id SERIAL PRIMARY KEY, " +
                    "title VARCHAR(255), " +
                    "description TEXT)");

            // Create Recipe Tags table
            stmt.execute("CREATE TABLE IF NOT EXISTS recipe_tags (" +
                    "recipe_id INT, " +
                    "tag VARCHAR(50), " +
                    "FOREIGN KEY (recipe_id) REFERENCES recipes(id))");

            // Create Recipe Ingredients link table
            stmt.execute("CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                    "recipe_id INT, " +
                    "ingredient_id INT, " +
                    "FOREIGN KEY (recipe_id) REFERENCES recipes(id), " +
                    "FOREIGN KEY (ingredient_id) REFERENCES ingredients(id))");

            // Create MealPlans table
            stmt.execute("CREATE TABLE IF NOT EXISTS meal_plans (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "date DATE UNIQUE NOT NULL)");

            // Create MealPlan-Recipes Link Table (with MealType)
            // Dropping table first to ensure schema update in dev environment
            stmt.execute("CREATE TABLE IF NOT EXISTS meal_plan_recipes (" +
                    "meal_plan_id BIGINT, " +
                    "recipe_id BIGINT, " +
                    "meal_type VARCHAR(50), " +
                    "FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id), " +
                    "FOREIGN KEY (recipe_id) REFERENCES recipes(id))");
        }
    }

    @Override
    public RecipeRepository getRecipeRepository() {
        return new H2RecipeRepository(connection);
    }

    @Override
    public IngredientRepository getIngredientRepository() {
        return new H2IngredientRepository(connection);
    }

    @Override
    public MealPlanRepository getMealPlanRepository() {
        return new H2MealPlanRepository(connection);
    }
}
