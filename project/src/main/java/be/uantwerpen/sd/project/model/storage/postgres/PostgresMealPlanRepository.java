package be.uantwerpen.sd.project.model.storage.postgres;

import be.uantwerpen.sd.project.model.storage.MealPlanRepository;

import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PostgresMealPlanRepository implements MealPlanRepository {
    private final Connection connection;
    private final RecipeRepository recipeRepository;

    public PostgresMealPlanRepository(Connection connection, RecipeRepository recipeRepository) {
        this.connection = connection;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public MealPlan save(MealPlan plan) {
        // upsert plan then rewrite all slot links
        try {
            Long existingId = findPlanIdByDate(plan.date());
            long planId = existingId == null ? insertPlan(plan.date()) : existingId;

            if (existingId != null) {
                try (PreparedStatement delete = connection
                        .prepareStatement("DELETE FROM meal_plan_recipes WHERE meal_plan_id = ?")) {
                    delete.setLong(1, planId);
                    delete.executeUpdate();
                }
            }

            String linkSql = "INSERT INTO meal_plan_recipes (meal_plan_id, recipe_id, meal_type) VALUES (?, ?, ?)";
            try (PreparedStatement linkStmt = connection.prepareStatement(linkSql)) {
                for (Map.Entry<MealType, List<Recipe>> entry : plan.meals().entrySet()) {
                    for (Recipe recipe : entry.getValue()) {
                        linkStmt.setLong(1, planId);
                        linkStmt.setLong(2, recipe.id());
                        linkStmt.setString(3, entry.getKey().name());
                        linkStmt.addBatch();
                    }
                }
                linkStmt.executeBatch();
            }

            return new MealPlan(planId, plan.date(), plan.meals());
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<MealPlan> findByDate(LocalDate date) {
        try {
            String sql = "SELECT * FROM meal_plans WHERE date = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(date));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        Map<MealType, List<Recipe>> meals = getMealsForPlan(id);
                        return Optional.of(new MealPlan(id, date, meals));
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<MealPlan> findAll() {
        List<MealPlan> mealPlans = new ArrayList<>();
        try {
            String sql = "SELECT * FROM meal_plans";
            try (java.sql.Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    Map<MealType, List<Recipe>> meals = getMealsForPlan(id);
                    mealPlans.add(new MealPlan(id, date, meals));
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
        return mealPlans;
    }

    private Long findPlanIdByDate(LocalDate date) throws java.sql.SQLException {
        String sql = "SELECT id FROM meal_plans WHERE date = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        return null;
    }

    private long insertPlan(LocalDate date) throws java.sql.SQLException {
        String sql = "INSERT INTO meal_plans (date) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            }
        }
    }

    private Map<MealType, List<Recipe>> getMealsForPlan(long mealPlanId) {
        // load linked recipes per meal type
        Map<MealType, List<Recipe>> meals = new java.util.HashMap<>();

        String sql = "SELECT recipe_id, meal_type FROM meal_plan_recipes WHERE meal_plan_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, mealPlanId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long recipeId = rs.getLong("recipe_id");
                    MealType type = MealType.valueOf(rs.getString("meal_type"));
                    Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
                    meals.computeIfAbsent(type, k -> new ArrayList<>()).add(recipe);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
        return meals;
    }
}
