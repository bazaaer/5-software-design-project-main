package be.uantwerpen.sd.project.model.storage.h2;

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

public class H2RecipeRepository implements RecipeRepository {
    private Connection connection;

    public H2RecipeRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Recipe save(Recipe recipe) {
        // Check if exists
        Optional<Recipe> existing = findByTitle(recipe.title());
        if (existing.isPresent()) {
            System.out.println("[H2] Recipe already exists: " + recipe.title());
            return existing.get();
        }

        long recipeId = -1;
        try {
            // 1. Save Recipe
            String sql = "INSERT INTO recipes (title, description) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, recipe.title());
                stmt.setString(2, recipe.description());
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        recipeId = generatedKeys.getLong(1);
                        System.out.println("[H2] Saved recipe: " + recipe.title() + " with ID: " + recipeId);
                    }
                }
            }

            if (recipeId != -1) {
                // 2. Save Tags
                String tagSql = "INSERT INTO recipe_tags (recipe_id, tag) VALUES (?, ?)";
                try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
                    for (String tag : recipe.tags()) {
                        tagStmt.setLong(1, recipeId);
                        tagStmt.setString(2, tag);
                        tagStmt.addBatch();
                    }
                    tagStmt.executeBatch();
                }

                // 3. Save Ingredients Link
                String linkSql = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id) VALUES (?, ?)";
                try (PreparedStatement linkStmt = connection.prepareStatement(linkSql)) {
                    for (Ingredient ingredient : recipe.ingredients()) {
                        if (ingredient.id() != null) {
                            linkStmt.setLong(1, recipeId);
                            linkStmt.setLong(2, ingredient.id());
                            linkStmt.addBatch();
                        }
                    }
                    linkStmt.executeBatch();
                }

                return new Recipe(recipeId, recipe.title(), recipe.description(), recipe.tags(), recipe.ingredients());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipe; // Return original if failed
    }

    @Override
    public List<Recipe> findAll() {
        List<Recipe> recipes = new ArrayList<>();
        try {
            String sql = "SELECT * FROM recipes";
            try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String description = rs.getString("description");

                    // Fetch Tags
                    List<String> tags = new ArrayList<>();
                    String tagSql = "SELECT tag FROM recipe_tags WHERE recipe_id = ?";
                    try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
                        tagStmt.setLong(1, id);
                        try (ResultSet tagRs = tagStmt.executeQuery()) {
                            while (tagRs.next()) {
                                tags.add(tagRs.getString("tag"));
                            }
                        }
                    }

                    // Fetch Ingredients
                    List<Ingredient> ingredients = new ArrayList<>();
                    String ingSql = "SELECT i.* FROM ingredients i " +
                            "JOIN recipe_ingredients ri ON i.id = ri.ingredient_id " +
                            "WHERE ri.recipe_id = ?";
                    try (PreparedStatement ingStmt = connection.prepareStatement(ingSql)) {
                        ingStmt.setLong(1, id);
                        try (ResultSet ingRs = ingStmt.executeQuery()) {
                            while (ingRs.next()) {
                                Ingredient ingredient = new Ingredient(
                                        ingRs.getLong("id"),
                                        ingRs.getString("name"),
                                        ingRs.getDouble("amount"),
                                        ingRs.getString("unit"));
                                ingredients.add(ingredient);
                            }
                        }
                    }

                    recipes.add(new Recipe(id, title, description, tags, ingredients));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[H2] Found " + recipes.size() + " recipes");
        return recipes;
    }

    private Optional<Recipe> findByTitle(String title) {
        try {
            String sql = "SELECT * FROM recipes WHERE title = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, title);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        String description = rs.getString("description");

                        // Fetch Tags
                        List<String> tags = new ArrayList<>();
                        String tagSql = "SELECT tag FROM recipe_tags WHERE recipe_id = ?";
                        try (PreparedStatement tagStmt = connection.prepareStatement(tagSql)) {
                            tagStmt.setLong(1, id);
                            try (ResultSet tagRs = tagStmt.executeQuery()) {
                                while (tagRs.next()) {
                                    tags.add(tagRs.getString("tag"));
                                }
                            }
                        }

                        // Fetch Ingredients
                        List<Ingredient> ingredients = new ArrayList<>();
                        String ingSql = "SELECT i.* FROM ingredients i " +
                                "JOIN recipe_ingredients ri ON i.id = ri.ingredient_id " +
                                "WHERE ri.recipe_id = ?";
                        try (PreparedStatement ingStmt = connection.prepareStatement(ingSql)) {
                            ingStmt.setLong(1, id);
                            try (ResultSet ingRs = ingStmt.executeQuery()) {
                                while (ingRs.next()) {
                                    Ingredient ingredient = new Ingredient(
                                            ingRs.getLong("id"),
                                            ingRs.getString("name"),
                                            ingRs.getDouble("amount"),
                                            ingRs.getString("unit"));
                                    ingredients.add(ingredient);
                                }
                            }
                        }
                        return Optional.of(new Recipe(id, title, description, tags, ingredients));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
