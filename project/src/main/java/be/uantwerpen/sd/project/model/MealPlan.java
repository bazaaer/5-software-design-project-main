package be.uantwerpen.sd.project.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record MealPlan(Long id, LocalDate date, Map<MealType, List<Recipe>> meals) {
    public MealPlan(LocalDate date, Map<MealType, List<Recipe>> meals) {
        this(null, date, meals);
    }
}
