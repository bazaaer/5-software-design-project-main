package be.uantwerpen.sd.project.controller;

import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.grocery.GroceryItem;
import be.uantwerpen.sd.project.model.grocery.GroceryListService;
import be.uantwerpen.sd.project.model.grocery.GroceryListStrategy;
import be.uantwerpen.sd.project.model.grocery.MetricGroceryListStrategy;
import be.uantwerpen.sd.project.model.storage.MealPlanRepository;
import be.uantwerpen.sd.project.model.storage.RecipeRepository;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import java.beans.PropertyChangeListener;
import java.util.List;

public class MealPlannerController {
    private RecipeRepository recipeRepository;
    private MealPlanRepository mealPlanRepository;
    private GroceryListService groceryListService;

    public MealPlannerController(StorageFactory factory) {
        this(factory, new GroceryListService(new MetricGroceryListStrategy()));
    }

    public MealPlannerController(StorageFactory factory, GroceryListService groceryListService) {
        this.recipeRepository = factory.getRecipeRepository();
        this.mealPlanRepository = factory.getMealPlanRepository();
        this.groceryListService = groceryListService;
    }

    public Recipe addRecipe(String title, String description, List<String> tags, List<Ingredient> ingredients) {
        String safeTitle = title == null ? "" : title.trim();
        if (safeTitle.isBlank()) {
            throw new IllegalArgumentException("title pls");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("need some ingredients");
        }
        String safeDescription = description == null ? "" : description.trim();
        List<String> safeTags = tags == null ? List.of() : tags;
        Recipe recipe = new Recipe(safeTitle, safeDescription, safeTags, ingredients);
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(long id, String title, String description, List<String> tags, List<Ingredient> ingredients) {
        String safeTitle = title == null ? "" : title.trim();
        if (safeTitle.isBlank()) {
            throw new IllegalArgumentException("title pls");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("need some ingredients");
        }
        String safeDescription = description == null ? "" : description.trim();
        List<String> safeTags = tags == null ? List.of() : tags;
        return recipeRepository.save(new Recipe(id, safeTitle, safeDescription, safeTags, ingredients));
    }

    public void deleteRecipe(long id) {
        recipeRepository.deleteById(id);
    }

    public void addMealPlan(MealPlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("plan pls");
        }
        MealPlan saved = mealPlanRepository.save(plan);
        groceryListService.setMealPlan(saved);
    }

    public MealPlan saveMealPlan(MealPlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("plan pls");
        }
        return mealPlanRepository.save(plan);
    }

    public java.util.Optional<MealPlan> getMealPlan(java.time.LocalDate date) {
        return mealPlanRepository.findByDate(date);
    }

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public void selectMealPlan(MealPlan plan) {
        groceryListService.setMealPlan(plan);
    }

    public List<GroceryItem> getGroceryList() {
        return groceryListService.getGroceryList();
    }

    public void setGroceryListStrategy(GroceryListStrategy strategy) {
        groceryListService.setStrategy(strategy);
    }

    public void addExtraGroceryItem(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("ingredient pls");
        }
        groceryListService.addExtraItem(ingredient);
    }

    public void removeExtraGroceryItem(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("ingredient pls");
        }
        groceryListService.removeExtraItem(ingredient);
    }

    public void setGroceryItemBought(String name, String unit, boolean bought) {
        groceryListService.setBought(name, unit, bought);
    }

    public void addGroceryListListener(PropertyChangeListener listener) {
        groceryListService.addPropertyChangeListener(listener);
    }

    public void removeGroceryListListener(PropertyChangeListener listener) {
        groceryListService.removePropertyChangeListener(listener);
    }
}
