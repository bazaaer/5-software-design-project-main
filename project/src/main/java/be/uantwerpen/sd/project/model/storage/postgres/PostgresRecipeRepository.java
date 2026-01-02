package be.uantwerpen.sd.project.model.storage.postgres;

import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.Ingredient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresRecipeRepository implements RecipeRepository {
    private final Connection connection;

    public PostgresRecipeRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Recipe save(Recipe recipe) {
        // insert recipe + tags + ingredients, then reload
        if (recipe.id() != null) {
            return update(recipe);
        }
        try {
            String sql = "INSERT INTO recipes (title, description) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, recipe.title());
                stmt.setString(2, recipe.description());
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    generatedKeys.next();
                    long recipeId = generatedKeys.getLong(1);

                    String tagSql = "INSERT INTO recipe_tags (recipe_id, tag) VALUES (?, ?)";
                    try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
                        for (String tag : recipe.tags()) {
                            tagStmt.setLong(1, recipeId);
                            tagStmt.setString(2, tag);
                            tagStmt.addBatch();
                        }
                        tagStmt.executeBatch();
                    }

                    String ingSql = "INSERT INTO recipe_ingredients (recipe_id, name, amount, unit) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ingStmt = connection.prepareStatement(ingSql)) {
                        for (Ingredient ingredient : recipe.ingredients()) {
                            ingStmt.setLong(1, recipeId);
                            ingStmt.setString(2, ingredient.name());
                            ingStmt.setDouble(3, ingredient.amount());
                            ingStmt.setString(4, ingredient.unit());
                            ingStmt.addBatch();
                        }
                        ingStmt.executeBatch();
                    }

                    return findById(recipeId).orElseThrow();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe update(Recipe recipe) {
        // update base row, then replace tags/ingredients
        try {
            String sql = "UPDATE recipes SET title = ?, description = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, recipe.title());
                stmt.setString(2, recipe.description());
                stmt.setLong(3, recipe.id());
                stmt.executeUpdate();
            }

            try (PreparedStatement deleteTags = connection.prepareStatement("DELETE FROM recipe_tags WHERE recipe_id = ?")) {
                deleteTags.setLong(1, recipe.id());
                deleteTags.executeUpdate();
            }
            try (PreparedStatement deleteIngredients = connection
                    .prepareStatement("DELETE FROM recipe_ingredients WHERE recipe_id = ?")) {
                deleteIngredients.setLong(1, recipe.id());
                deleteIngredients.executeUpdate();
            }

            String tagSql = "INSERT INTO recipe_tags (recipe_id, tag) VALUES (?, ?)";
            try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
                for (String tag : recipe.tags()) {
                    tagStmt.setLong(1, recipe.id());
                    tagStmt.setString(2, tag);
                    tagStmt.addBatch();
                }
                tagStmt.executeBatch();
            }

            String ingSql = "INSERT INTO recipe_ingredients (recipe_id, name, amount, unit) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ingStmt = connection.prepareStatement(ingSql)) {
                for (Ingredient ingredient : recipe.ingredients()) {
                    ingStmt.setLong(1, recipe.id());
                    ingStmt.setString(2, ingredient.name());
                    ingStmt.setDouble(3, ingredient.amount());
                    ingStmt.setString(4, ingredient.unit());
                    ingStmt.addBatch();
                }
                ingStmt.executeBatch();
            }

            return findById(recipe.id()).orElseThrow();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            try (PreparedStatement deleteTags = connection.prepareStatement("DELETE FROM recipe_tags WHERE recipe_id = ?")) {
                deleteTags.setLong(1, id);
                deleteTags.executeUpdate();
            }
            try (PreparedStatement deleteIngredients = connection
                    .prepareStatement("DELETE FROM recipe_ingredients WHERE recipe_id = ?")) {
                deleteIngredients.setLong(1, id);
                deleteIngredients.executeUpdate();
            }
            try (PreparedStatement deleteRecipe = connection.prepareStatement("DELETE FROM recipes WHERE id = ?")) {
                deleteRecipe.setLong(1, id);
                deleteRecipe.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Recipe> findById(long id) {
        try {
            String sql = "SELECT * FROM recipes WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String title = rs.getString("title");
                        String description = rs.getString("description");
                        List<String> tags = loadTags(id);
                        List<Ingredient> ingredients = loadIngredients(id);
                        return Optional.of(new Recipe(id, title, description, tags, ingredients));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Recipe> findAll() {
        List<Recipe> recipes = new ArrayList<>();
        try {
            String sql = "SELECT * FROM recipes";
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    List<String> tags = loadTags(id);
                    List<Ingredient> ingredients = loadIngredients(id);
                    recipes.add(new Recipe(id, title, description, tags, ingredients));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return recipes;
    }

    private List<String> loadTags(long recipeId) throws SQLException {
        List<String> tags = new ArrayList<>();
        String tagSql = "SELECT tag FROM recipe_tags WHERE recipe_id = ? ORDER BY tag";
        try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
            tagStmt.setLong(1, recipeId);
            try (ResultSet tagRs = tagStmt.executeQuery()) {
                while (tagRs.next()) {
                    tags.add(tagRs.getString("tag"));
                }
            }
        }
        return tags;
    }

    private List<Ingredient> loadIngredients(long recipeId) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        String ingSql = "SELECT name, amount, unit FROM recipe_ingredients WHERE recipe_id = ?";
        try (PreparedStatement ingStmt = connection.prepareStatement(ingSql)) {
            ingStmt.setLong(1, recipeId);
            try (ResultSet ingRs = ingStmt.executeQuery()) {
                while (ingRs.next()) {
                    ingredients.add(
                            new Ingredient(ingRs.getString("name"), ingRs.getDouble("amount"), ingRs.getString("unit")));
                }
            }
        }
        return ingredients;
    }
}
