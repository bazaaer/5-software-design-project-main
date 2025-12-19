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
        Recipe recipe = new Recipe(title, description, tags, ingredients);
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(long id, String title, String description, List<String> tags, List<Ingredient> ingredients) {
        return recipeRepository.save(new Recipe(id, title, description, tags, ingredients));
    }

    public void deleteRecipe(long id) {
        recipeRepository.deleteById(id);
    }

    public void addMealPlan(MealPlan plan) {
        MealPlan saved = mealPlanRepository.save(plan);
        groceryListService.setMealPlan(saved);
    }

    public MealPlan saveMealPlan(MealPlan plan) {
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
        groceryListService.addExtraItem(ingredient);
    }

    public void removeExtraGroceryItem(Ingredient ingredient) {
        groceryListService.removeExtraItem(ingredient);
    }

    public void setGroceryItemBought(String name, String unit, boolean bought) {
        groceryListService.setBought(name, unit, bought);
    }
}
