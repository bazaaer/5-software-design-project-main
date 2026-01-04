package be.uantwerpen.sd.project.model.storage.h2;

import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.storage.GroceryDeltaRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class H2GroceryDeltaRepository implements GroceryDeltaRepository {
    private final Connection connection;

    public H2GroceryDeltaRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Set<BoughtKey> findBoughtKeys(LocalDate weekStart) {
        Set<BoughtKey> keys = new HashSet<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT name_key, dimension FROM grocery_bought WHERE week_start = ?")) {
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    keys.add(new BoughtKey(rs.getString("name_key"), rs.getString("dimension")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return keys;
    }

    @Override
    public void setBought(LocalDate weekStart, BoughtKey key, boolean bought) {
        try {
            if (bought) {
                try (PreparedStatement ps = connection.prepareStatement(
                        "MERGE INTO grocery_bought (week_start, name_key, dimension) KEY (week_start, name_key, dimension) VALUES (?, ?, ?)")) {
                    ps.setDate(1, java.sql.Date.valueOf(weekStart));
                    ps.setString(2, key.nameKey());
                    ps.setString(3, key.dimension());
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(
                        "DELETE FROM grocery_bought WHERE week_start = ? AND name_key = ? AND dimension = ?")) {
                    ps.setDate(1, java.sql.Date.valueOf(weekStart));
                    ps.setString(2, key.nameKey());
                    ps.setString(3, key.dimension());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Ingredient> findExtraItems(LocalDate weekStart) {
        List<Ingredient> items = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT name, amount, unit FROM grocery_extras WHERE week_start = ? ORDER BY id")) {
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Ingredient(rs.getString("name"), rs.getDouble("amount"), rs.getString("unit")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    @Override
    public void addExtraItem(LocalDate weekStart, Ingredient ingredient) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO grocery_extras (week_start, name, amount, unit) VALUES (?, ?, ?, ?)")) {
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            ps.setString(2, ingredient.name());
            ps.setDouble(3, ingredient.amount());
            ps.setString(4, ingredient.unit());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeExtraItem(LocalDate weekStart, Ingredient ingredient) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM grocery_extras WHERE week_start = ? AND name = ? AND unit = ?")) {
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            ps.setString(2, ingredient.name());
            ps.setString(3, ingredient.unit());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
