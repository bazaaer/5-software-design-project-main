package be.uantwerpen.sd.project.model.storage;

import be.uantwerpen.sd.project.model.MealPlan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPlanRepository {
    MealPlan save(MealPlan plan);

    Optional<MealPlan> findByDate(LocalDate date);

    List<MealPlan> findAll();
}
