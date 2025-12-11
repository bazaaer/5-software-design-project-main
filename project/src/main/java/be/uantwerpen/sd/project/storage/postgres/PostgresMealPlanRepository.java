package be.uantwerpen.sd.project.storage.postgres;

import be.uantwerpen.sd.project.storage.MealPlanRepository;

import be.uantwerpen.sd.project.data.MealPlan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresMealPlanRepository implements MealPlanRepository {
    @Override
    public MealPlan save(MealPlan plan) {
        System.out.println("[Postgres] Saved meal plan for date: " + plan.date());
        return plan;
    }

    @Override
    public Optional<MealPlan> findByDate(LocalDate date) {
        System.out.println("[Postgres] Finding meal plan by date: " + date);
        return Optional.empty();
    }

    @Override
    public List<MealPlan> findAll() {
        System.out.println("[Postgres] Finding all meal plans");
        return new ArrayList<>();
    }
}
