package be.uantwerpen.sd.project.model.grocery;

import be.uantwerpen.sd.project.model.Ingredient;
import java.util.List;

public interface GroceryListStrategy {
    List<Ingredient> combine(List<Ingredient> ingredients);
}

