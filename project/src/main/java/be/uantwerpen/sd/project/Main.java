package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.MealPlannerController;
import be.uantwerpen.sd.project.data.Ingredient;
import be.uantwerpen.sd.project.data.MealPlan;
import be.uantwerpen.sd.project.data.Recipe;
import be.uantwerpen.sd.project.storage.StorageFactory;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Meal Planner...");

        // 1. Get the StorageFactory (Singleton)
        // This will automatically choose H2 or Postgres based on availability
        StorageFactory storageFactory = StorageFactory.getInstance();

        // 2. Create the Controller
        MealPlannerController controller = new MealPlannerController(storageFactory);

        // 3. Add a sample recipe
        System.out.println("\n--- Adding Recipe ---");
        Ingredient pasta = new Ingredient("Pasta", 500, "g");
        Ingredient sauce = new Ingredient("Tomato Sauce", 1, "jar");

        controller.addRecipe(
                "Spaghetti Bolognese",
                "Classic Italian pasta dish",
                Arrays.asList("Dinner", "Italian"),
                Arrays.asList(pasta, sauce));

        // 4. List all recipes
        System.out.println("\n--- All Recipes ---");
        List<Recipe> recipes = controller.getAllRecipes();
        for (Recipe recipe : recipes) {
            System.out.println("Recipe: " + recipe.title());
            System.out.println("  Description: " + recipe.description());
            System.out.println("  Tags: " + recipe.tags());
            System.out.println("  Ingredients:");
            for (Ingredient ingredient : recipe.ingredients()) {
                System.out.println("    - " + ingredient.amount() + " " + ingredient.unit() + " " + ingredient.name());
            }
        }

        // 5. Create and Save a MealPlan
        System.out.println("\n--- Adding Meal Plan ---");
        java.time.LocalDate today = java.time.LocalDate.now();
        if (controller.getMealPlan(today).isEmpty()) {
            MealPlan plan = new MealPlan(today, recipes);
            controller.addMealPlan(plan);
            System.out.println("Added new meal plan for " + today);
        } else {
            System.out.println("Meal plan for " + today + " already exists.");
        }

        // 6. List all Meal Plans
        System.out.println("\n--- All Meal Plans ---");
        List<MealPlan> plans = controller.getAllMealPlans(); // And this one
        for (MealPlan p : plans) {
            System.out.println("Meal Plan for: " + p.date());
            for (Recipe r : p.recipes()) {
                System.out.println("  - " + r.title());
            }
        }
    }
}
