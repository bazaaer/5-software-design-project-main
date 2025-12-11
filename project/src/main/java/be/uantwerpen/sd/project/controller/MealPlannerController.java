package be.uantwerpen.sd.project.controller;

import be.uantwerpen.sd.project.data.Ingredient;
import be.uantwerpen.sd.project.data.MealPlan;
import be.uantwerpen.sd.project.data.Recipe;
import be.uantwerpen.sd.project.storage.IngredientRepository;
import be.uantwerpen.sd.project.storage.MealPlanRepository;
import be.uantwerpen.sd.project.storage.RecipeRepository;
import be.uantwerpen.sd.project.storage.StorageFactory;
import java.util.ArrayList;
import java.util.List;

public class MealPlannerController {
    private RecipeRepository recipeRepository;
    private IngredientRepository ingredientRepository;
    private MealPlanRepository mealPlanRepository;

    public MealPlannerController(StorageFactory factory) {
        this.recipeRepository = factory.getRecipeRepository();
        this.ingredientRepository = factory.getIngredientRepository();
        this.mealPlanRepository = factory.getMealPlanRepository();
    }

    public void addRecipe(String title, String description, List<String> tags, List<Ingredient> ingredients) {
        // 1. Save Ingredients first (to get IDs)
        List<Ingredient> savedIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            savedIngredients.add(ingredientRepository.save(ingredient));
        }

        // 2. Create and Save Recipe
        Recipe recipe = new Recipe(title, description, tags, savedIngredients);
        recipeRepository.save(recipe);
    }

    public void addMealPlan(MealPlan plan) {
        mealPlanRepository.save(plan);
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
}
