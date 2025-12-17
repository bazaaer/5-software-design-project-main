package be.uantwerpen.sd.project.model.storage.h2;

import be.uantwerpen.sd.project.model.storage.IngredientRepository;
import be.uantwerpen.sd.project.model.Ingredient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2IngredientRepository implements IngredientRepository {
    private Connection connection;

    public H2IngredientRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        // Check if exists
        Optional<Ingredient> existing = findByName(ingredient.name());
        if (existing.isPresent()) {
            System.out.println("[H2] Ingredient already exists: " + ingredient.name());
            return existing.get();
        }

        try {
            String sql = "INSERT INTO ingredients (name, amount, unit) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, ingredient.name());
                stmt.setDouble(2, ingredient.amount());
                stmt.setString(3, ingredient.unit());
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long id = generatedKeys.getLong(1);
                        System.out.println("[H2] Saved ingredient: " + ingredient.name() + " with ID: " + id);
                        return new Ingredient(id, ingredient.name(), ingredient.amount(), ingredient.unit());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredient; // Return original if failed (or handle error better)
    }

    @Override
    public Optional<Ingredient> findByName(String name) {
        try {
            String sql = "SELECT * FROM ingredients WHERE name = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Ingredient ingredient = new Ingredient(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getDouble("amount"),
                                rs.getString("unit"));
                        return Optional.of(ingredient);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            String sql = "SELECT * FROM ingredients";
            try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDouble("amount"),
                            rs.getString("unit"));
                    ingredients.add(ingredient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[H2] Found " + ingredients.size() + " ingredients");
        return ingredients;
    }
}
