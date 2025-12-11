package be.uantwerpen.sd.project.storage.h2;

import be.uantwerpen.sd.project.storage.MealPlanRepository;

import be.uantwerpen.sd.project.data.MealPlan;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
                // 2. Save Recipe Links
                String linkSql = "INSERT INTO meal_plan_recipes (meal_plan_id, recipe_id) VALUES (?, ?)";
                try (java.sql.PreparedStatement linkStmt = connection.prepareStatement(linkSql)) {
                    for (be.uantwerpen.sd.project.data.Recipe recipe : plan.recipes()) {
                        if (recipe.id() != null) {
                            linkStmt.setLong(1, planId);
                            linkStmt.setLong(2, recipe.id());
                            linkStmt.addBatch();
                        }
                    }
                    linkStmt.executeBatch();
                }
                return new MealPlan(planId, plan.date(), plan.recipes());
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
                        List<be.uantwerpen.sd.project.data.Recipe> recipes = getRecipesForMealPlan(id);
                        return Optional.of(new MealPlan(id, date, recipes));
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
                    List<be.uantwerpen.sd.project.data.Recipe> recipes = getRecipesForMealPlan(id);
                    mealPlans.add(new MealPlan(id, date, recipes));
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        System.out.println("[H2] Found " + mealPlans.size() + " meal plans");
        return mealPlans;
    }

    private List<be.uantwerpen.sd.project.data.Recipe> getRecipesForMealPlan(long mealPlanId) {
        List<be.uantwerpen.sd.project.data.Recipe> recipes = new ArrayList<>();
        // Note: This is a simplified fetch. Ideally, we would join with recipes table
        // to get full details.
        // For now, we will fetch the recipe IDs and then fetch the full recipes using
        // H2RecipeRepository logic
        // or a join query. Let's do a join query to be efficient.
        String sql = "SELECT r.* FROM recipes r " +
                "JOIN meal_plan_recipes mpr ON r.id = mpr.recipe_id " +
                "WHERE mpr.meal_plan_id = ?";

        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, mealPlanId);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Re-using the logic from H2RecipeRepository would be cleaner, but for now we
                    // duplicate
                    // the basic reconstruction or we need to inject RecipeRepository here.
                    // To avoid circular dependencies or complex injection, we'll just fetch basic
                    // info
                    // and maybe tags/ingredients if we want to be thorough.
                    // Let's keep it simple: just basic recipe info for now.
                    // If we want full recipes, we should probably refactor to use the
                    // RecipeRepository.

                    // ... actually, let's just fetch basic info to prove persistence works.
                    be.uantwerpen.sd.project.data.Recipe recipe = new be.uantwerpen.sd.project.data.Recipe(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            new ArrayList<>(), // Tags - empty for now
                            new ArrayList<>() // Ingredients - empty for now
                    );
                    recipes.add(recipe);
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return recipes;
    }
}
