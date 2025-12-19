package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.grocery.GroceryItem;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RenderPort {
    void showRecipes(List<Recipe> recipes);

    void showWeek(LocalDate weekStart, Map<LocalDate, Map<MealType, List<Recipe>>> mealsByDay);

    void showGroceryList(List<GroceryItem> items);

    void clearRecipeInputs();

    void clearExtraItemInputs();

    void showStatus(String message);

    void showError(String message);
}
