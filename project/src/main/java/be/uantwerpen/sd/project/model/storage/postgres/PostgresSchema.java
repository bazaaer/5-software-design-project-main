package be.uantwerpen.sd.project.model.storage.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

final class PostgresSchema {
    private PostgresSchema() {
    }

    static void initialize(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS recipes (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "description TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS recipe_tags (" +
                    "recipe_id BIGINT NOT NULL, " +
                    "tag VARCHAR(50) NOT NULL, " +
                    "FOREIGN KEY (recipe_id) REFERENCES recipes(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                    "recipe_id BIGINT NOT NULL, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "amount DOUBLE PRECISION NOT NULL, " +
                    "unit VARCHAR(50) NOT NULL, " +
                    "FOREIGN KEY (recipe_id) REFERENCES recipes(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS meal_plans (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "date DATE UNIQUE NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS meal_plan_recipes (" +
                    "meal_plan_id BIGINT NOT NULL, " +
                    "recipe_id BIGINT NOT NULL, " +
                    "meal_type VARCHAR(50) NOT NULL, " +
                    "CONSTRAINT fk_meal_plan_recipes_plan FOREIGN KEY (meal_plan_id) REFERENCES meal_plans(id), " +
                    "CONSTRAINT fk_meal_plan_recipes_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS grocery_bought (" +
                    "week_start DATE NOT NULL, " +
                    "name_key VARCHAR(255) NOT NULL, " +
                    "dimension VARCHAR(20) NOT NULL, " +
                    "PRIMARY KEY (week_start, name_key, dimension))");

            stmt.execute("CREATE TABLE IF NOT EXISTS grocery_extras (" +
                    "id BIGSERIAL PRIMARY KEY, " +
                    "week_start DATE NOT NULL, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "amount DOUBLE PRECISION NOT NULL, " +
                    "unit VARCHAR(50) NOT NULL)");
        }
    }
}
