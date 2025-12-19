package be.uantwerpen.sd.project.model.grocery;

import be.uantwerpen.sd.project.model.Ingredient;

public record GroceryItem(Ingredient ingredient, boolean bought) {
}

