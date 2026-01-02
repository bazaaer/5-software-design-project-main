package be.uantwerpen.sd.project.view;

import be.uantwerpen.sd.project.controller.MealPlannerController;
import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.grocery.ImperialGroceryListStrategy;
import be.uantwerpen.sd.project.model.grocery.MetricGroceryListStrategy;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MealPlannerViewLogic implements PropertyChangeListener {
    private final MealPlannerController controller;
    private final RenderPort ui;

    private LocalDate weekStart;
    private final Map<LocalDate, Map<MealType, Recipe>> plannedWeek = new HashMap<>();

    public MealPlannerViewLogic(MealPlannerController controller, RenderPort ui) {
        this.controller = controller;
        this.ui = ui;

        // Observer: listen for grocery list updates
        controller.addGroceryListListener(this);

        ui.showRecipes(controller.getAllRecipes());
        onWeekSelected(LocalDate.now());
        ui.showGroceryList(controller.getGroceryList());
    }

    public void onWeekSelected(LocalDate anyDateInWeek) {
        weekStart = anyDateInWeek.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

        plannedWeek.clear();
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            MealPlan dayPlan = controller.getMealPlan(day).orElseGet(() -> new MealPlan(day, Map.of()));
            plannedWeek.put(day, normalizeMeals(dayPlan.meals()));
        }

        controller.selectMealPlan(aggregateWeekMealPlan());
        ui.showWeek(weekStart, snapshotWeek());
        ui.showStatus("Editing week of " + weekStart + " (Mon–Sun). Drag recipes into a slot, then click Save Week.");
    }

    public void onAddRecipe(String title, String description, String tagsRaw, List<Ingredient> ingredients) {
        String trimmedTitle = title.trim();
        String desc = description.trim();
        List<String> tags = parseTags(tagsRaw);
        controller.addRecipe(trimmedTitle, desc, tags, ingredients);
        ui.clearRecipeInputs();
        ui.showRecipes(controller.getAllRecipes());
    }

    public void onUpdateRecipe(long id, String title, String description, String tagsRaw, List<Ingredient> ingredients) {
        String trimmedTitle = title.trim();
        String desc = description.trim();
        List<String> tags = parseTags(tagsRaw);
        Recipe updated = controller.updateRecipe(id, trimmedTitle, desc, tags, ingredients);

        for (Map<MealType, Recipe> day : plannedWeek.values()) {
            for (MealType type : MealType.values()) {
                Recipe r = day.get(type);
                if (r != null && r.id().equals(updated.id())) {
                    day.put(type, updated);
                }
            }
        }

        controller.selectMealPlan(aggregateWeekMealPlan());
        ui.showRecipes(controller.getAllRecipes());
        ui.showWeek(weekStart, snapshotWeek());
        ui.showStatus("Updated recipe.");
    }

    public void onDeleteRecipe(long id) {
        controller.deleteRecipe(id);

        for (Map<MealType, Recipe> day : plannedWeek.values()) {
            for (MealType type : MealType.values()) {
                Recipe r = day.get(type);
                if (r != null && r.id() == id) {
                    day.put(type, null);
                }
            }
        }

        controller.selectMealPlan(aggregateWeekMealPlan());
        ui.showRecipes(controller.getAllRecipes());
        ui.showWeek(weekStart, snapshotWeek());
        ui.showStatus("Deleted recipe.");
    }

    public void onSetRecipeForSlot(LocalDate date, MealType mealType, Recipe recipe) {
        plannedWeek.computeIfAbsent(date, ignored -> emptySlots()).put(mealType, recipe);
        controller.selectMealPlan(aggregateWeekMealPlan());
        ui.showWeek(weekStart, snapshotWeek());
    }

    public void onSaveWeek() {
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            Map<MealType, Recipe> slots = plannedWeek.getOrDefault(day, emptySlots());
            controller.saveMealPlan(new MealPlan(day, snapshotMeals(slots)));
        }
        controller.selectMealPlan(aggregateWeekMealPlan());
        ui.showStatus("Saved week of " + weekStart + ".");
    }

    public void onSelectMetric() {
        controller.setGroceryListStrategy(new MetricGroceryListStrategy());
    }

    public void onSelectImperial() {
        controller.setGroceryListStrategy(new ImperialGroceryListStrategy());
    }

    public void onAddExtraItem(String name, String amountRaw, String unit) {
        double amount = Double.parseDouble(amountRaw);
        controller.addExtraGroceryItem(new Ingredient(name, amount, unit));
        ui.clearExtraItemInputs();
    }

    public void onSetBought(String name, String unit, boolean bought) {
        controller.setGroceryItemBought(name, unit, bought);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Observer hook for grocery list changes
        ui.showGroceryList(controller.getGroceryList());
    }

    private Map<LocalDate, Map<MealType, List<Recipe>>> snapshotWeek() {
        Map<LocalDate, Map<MealType, List<Recipe>>> snapshot = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            Map<MealType, Recipe> slots = plannedWeek.getOrDefault(day, emptySlots());
            snapshot.put(day, snapshotMeals(slots));
        }
        return snapshot;
    }

    private static Map<MealType, List<Recipe>> snapshotMeals(Map<MealType, Recipe> slots) {
        Map<MealType, List<Recipe>> snapshot = new EnumMap<>(MealType.class);
        for (MealType type : MealType.values()) {
            Recipe recipe = slots.get(type);
            snapshot.put(type, recipe == null ? List.of() : List.of(recipe));
        }
        return snapshot;
    }

    private MealPlan aggregateWeekMealPlan() {
        Map<MealType, List<Recipe>> combined = new EnumMap<>(MealType.class);
        for (MealType type : MealType.values()) {
            combined.put(type, new ArrayList<>());
        }
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            Map<MealType, Recipe> slots = plannedWeek.getOrDefault(day, emptySlots());
            for (MealType type : MealType.values()) {
                Recipe recipe = slots.get(type);
                if (recipe != null) {
                    combined.get(type).add(recipe);
                }
            }
        }
        return new MealPlan(weekStart, combined);
    }

    private static Map<MealType, Recipe> normalizeMeals(Map<MealType, List<Recipe>> meals) {
        Map<MealType, Recipe> normalized = emptySlots();
        for (MealType type : MealType.values()) {
            List<Recipe> recipes = meals.getOrDefault(type, List.of());
            normalized.put(type, recipes.isEmpty() ? null : recipes.getFirst());
        }
        return normalized;
    }

    private static Map<MealType, Recipe> emptySlots() {
        Map<MealType, Recipe> slots = new EnumMap<>(MealType.class);
        for (MealType type : MealType.values()) {
            slots.put(type, null);
        }
        return slots;
    }

    private static List<String> parseTags(String raw) {
        String[] parts = raw.split(",");
        List<String> tags = new ArrayList<>();
        for (String part : parts) {
            String tag = part.trim();
            tags.add(tag);
        }
        return tags;
    }
}
