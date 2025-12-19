package be.uantwerpen.sd.project.model.storage;

import be.uantwerpen.sd.project.model.storage.h2.H2StorageFactory;

public abstract class StorageFactory {

    // The "Smart Switch" (Singleton/Factory Method)
    private static StorageFactory instance;

    public static synchronized StorageFactory getInstance() {
        if (instance == null) {
            instance = H2StorageFactory.getInstance();
        }
        return instance;
    }

    // The Abstract Method (What we produce)
    public abstract RecipeRepository getRecipeRepository();

    public abstract MealPlanRepository getMealPlanRepository();

    public abstract GroceryDeltaRepository getGroceryDeltaRepository();
}
