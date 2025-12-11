package be.uantwerpen.sd.project.data;

import java.time.LocalDate;
import java.util.List;

public record MealPlan(Long id, LocalDate date, List<Recipe> recipes) {
    public MealPlan(LocalDate date, List<Recipe> recipes) {
        this(null, date, recipes);
    }
}
