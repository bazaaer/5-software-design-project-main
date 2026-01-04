package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.MealPlannerController;
import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.grocery.GroceryItem;
import be.uantwerpen.sd.project.model.grocery.GroceryListService;
import be.uantwerpen.sd.project.model.grocery.ImperialGroceryListStrategy;
import be.uantwerpen.sd.project.model.grocery.MetricGroceryListStrategy;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Meal Planner...");

        StorageFactory storageFactory = StorageFactory.getInstance();

        GroceryListService model = new GroceryListService(
                new MetricGroceryListStrategy(),
                storageFactory.getGroceryDeltaRepository());
        MealPlannerController controller = new MealPlannerController(storageFactory, model);

        model.addPropertyChangeListener(evt -> {
            if (!"groceryList".equals(evt.getPropertyName())) {
                return;
            }
            System.out.println("\n--- Grocery List Updated ---");
            for (GroceryItem item : controller.getGroceryList()) {
                String checkbox = item.bought() ? "[x]" : "[ ]";
                Ingredient ingredient = item.ingredient();
                System.out.println(
                        checkbox + " " + ingredient.name() + " — " + ingredient.amount() + " " + ingredient.unit());
            }
        });

        System.out.println("\n--- Adding Demo Recipes ---");
        Recipe cerealWithMilk = controller.addRecipe(
                "Cereal with Milk",
                "Classic breakfast. Crunchy cereal + cold milk.",
                Arrays.asList("quick"),
                Arrays.asList(new Ingredient("Cereal", 100, "g"), new Ingredient("Milk", 250, "ml")));

        Recipe spaghettiBolognese = controller.addRecipe(
                "Spaghetti Bolognese",
                "Classic Italian comfort food.",
                Arrays.asList("italian"),
                Arrays.asList(
                        new Ingredient("Pasta", 500, "g"),
                        new Ingredient("Tomato Sauce", 500, "ml"),
                        new Ingredient("Minced Meat", 300, "g")));

        Recipe bananaSplit = controller.addRecipe(
                "Banana Split",
                "Dessert disguised as a snack.",
                Arrays.asList("sweet"),
                Arrays.asList(
                        new Ingredient("Banana", 200, "g"),
                        new Ingredient("Ice Cream", 300, "g"),
                        new Ingredient("Chocolate Sauce", 50, "ml")));

        List<Recipe> demoRecipes = List.of(cerealWithMilk, spaghettiBolognese, bananaSplit);

        System.out.println("\n--- Demo Recipes (Just Added) ---");
        for (Recipe recipe : demoRecipes) {
            System.out.println("Recipe: " + recipe.title());
            System.out.println("  Description: " + recipe.description());
            System.out.println("  Tags: " + recipe.tags());
            System.out.println("  Ingredients:");
            for (Ingredient ingredient : recipe.ingredients()) {
                System.out.println("    - " + ingredient.amount() + " " + ingredient.unit() + " " + ingredient.name());
            }
        }

        System.out.println("\n--- Creating A Meal Plan ---");
        LocalDate demoDate = LocalDate.now().plusDays(365);
        while (controller.getMealPlan(demoDate).isPresent()) {
            demoDate = demoDate.plusDays(1);
        }

        Map<MealType, List<Recipe>> meals = new java.util.HashMap<>();
        meals.put(MealType.BREAKFAST, List.of(cerealWithMilk));
        meals.put(MealType.DINNER, List.of(spaghettiBolognese));
        meals.put(MealType.SNACK, List.of(bananaSplit));

        MealPlan plan = new MealPlan(demoDate, meals);
        controller.addMealPlan(plan);
        System.out.println("Planned meals for " + demoDate);

        System.out.println("\n--- Grocery List Actions (Metric) ---");
        controller.setGroceryListStrategy(new MetricGroceryListStrategy());
        controller.addExtraGroceryItem(new Ingredient("Soap", 1, "oz"));
        controller.setGroceryItemBought("Pasta", "g", true);

        System.out.println("\n--- Switching to Imperial Strategy ---");
        controller.setGroceryListStrategy(new ImperialGroceryListStrategy());
    }
}
