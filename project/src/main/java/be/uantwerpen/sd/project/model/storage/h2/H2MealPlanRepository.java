package be.uantwerpen.sd.project.model.storage.h2;

import be.uantwerpen.sd.project.model.storage.MealPlanRepository;

import be.uantwerpen.sd.project.model.MealPlan;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class H2MealPlanRepository implements MealPlanRepository {
    private Connection connection;

    public H2MealPlanRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public MealPlan save(MealPlan plan) {
        long planId = -1;
        try {
            // 1. Save MealPlan
            String sql = "INSERT INTO meal_plans (date) VALUES (?)";
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql,
                    java.sql.Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, java.sql.Date.valueOf(plan.date()));
                stmt.executeUpdate();

                try (java.sql.ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        planId = generatedKeys.getLong(1);
                        System.out.println("[H2] Saved meal plan for date: " + plan.date() + " with ID: " + planId);
                    }
                }
            }

            if (planId != -1) {
                // 2. Save Recipe Links with MealType
                String linkSql = "INSERT INTO meal_plan_recipes (meal_plan_id, recipe_id, meal_type) VALUES (?, ?, ?)";
                try (java.sql.PreparedStatement linkStmt = connection.prepareStatement(linkSql)) {
                    for (Map.Entry<be.uantwerpen.sd.project.model.MealType, List<be.uantwerpen.sd.project.model.Recipe>> entry : plan
                            .meals().entrySet()) {
                        be.uantwerpen.sd.project.model.MealType type = entry.getKey();
                        for (be.uantwerpen.sd.project.model.Recipe recipe : entry.getValue()) {
                            if (recipe.id() != null) {
                                linkStmt.setLong(1, planId);
                                linkStmt.setLong(2, recipe.id());
                                linkStmt.setString(3, type.name());
                                linkStmt.addBatch();
                            }
                        }
                    }
                    linkStmt.executeBatch();
                }
                return new MealPlan(planId, plan.date(), plan.meals());
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return plan;
    }

    @Override
    public Optional<MealPlan> findByDate(LocalDate date) {
        try {
            String sql = "SELECT * FROM meal_plans WHERE date = ?";
            try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(date));
                try (java.sql.ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        Map<be.uantwerpen.sd.project.model.MealType, List<be.uantwerpen.sd.project.model.Recipe>> meals = getMealsForPlan(
                                id);
                        return Optional.of(new MealPlan(id, date, meals));
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<MealPlan> findAll() {
        List<MealPlan> mealPlans = new ArrayList<>();
        try {
            String sql = "SELECT * FROM meal_plans";
            try (java.sql.Statement stmt = connection.createStatement();
                    java.sql.ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    LocalDate date = rs.getDate("date").toLocalDate();
                    Map<be.uantwerpen.sd.project.model.MealType, List<be.uantwerpen.sd.project.model.Recipe>> meals = getMealsForPlan(
                            id);
                    mealPlans.add(new MealPlan(id, date, meals));
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[H2] Found " + mealPlans.size() + " meal plans");
        return mealPlans;
    }

    private Map<be.uantwerpen.sd.project.model.MealType, List<be.uantwerpen.sd.project.model.Recipe>> getMealsForPlan(
            long mealPlanId) {
        Map<be.uantwerpen.sd.project.model.MealType, List<be.uantwerpen.sd.project.model.Recipe>> meals = new java.util.HashMap<>();

        String sql = "SELECT r.*, mpr.meal_type FROM recipes r " +
                "JOIN meal_plan_recipes mpr ON r.id = mpr.recipe_id " +
                "WHERE mpr.meal_plan_id = ?";

        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, mealPlanId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String typeStr = rs.getString("meal_type");
                    be.uantwerpen.sd.project.model.MealType type = be.uantwerpen.sd.project.model.MealType
                            .valueOf(typeStr);

                    be.uantwerpen.sd.project.model.Recipe recipe = new be.uantwerpen.sd.project.model.Recipe(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            new ArrayList<>(), // Tags - empty for now
                            new ArrayList<>() // Ingredients - empty for now
                    );

                    meals.computeIfAbsent(type, k -> new ArrayList<>()).add(recipe);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return meals;
    }
}
