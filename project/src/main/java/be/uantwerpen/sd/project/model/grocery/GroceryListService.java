package be.uantwerpen.sd.project.model.grocery;

import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealPlan;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.storage.GroceryDeltaRepository;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class GroceryListService {
    public static final String PROP_GROCERY_LIST = "groceryList";

    // Observer pattern: grocery list updates are pushed to listeners
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private GroceryListStrategy strategy;
    private MealPlan currentMealPlan;
    private LocalDate currentWeekStart;
    private final GroceryDeltaRepository deltaRepository;
    private final List<Ingredient> extraItems = new ArrayList<>();
    private final Set<Key> boughtKeys = new HashSet<>();
    private List<GroceryItem> groceryList = List.of();

    public GroceryListService(GroceryListStrategy strategy) {
        this(strategy, null);
    }

    public GroceryListService(GroceryListStrategy strategy, GroceryDeltaRepository deltaRepository) {
        this.strategy = strategy;
        this.deltaRepository = deltaRepository;
    }

    public void setMealPlan(MealPlan mealPlan) {
        LocalDate newWeekStart = weekStart(mealPlan);
        boolean weekChanged = !Objects.equals(currentWeekStart, newWeekStart);
        this.currentMealPlan = mealPlan;
        this.currentWeekStart = newWeekStart;
        if (weekChanged) {
            boughtKeys.clear();
            extraItems.clear();
            for (GroceryDeltaRepository.BoughtKey key : deltaRepository.findBoughtKeys(currentWeekStart)) {
                boughtKeys.add(Key.from(key));
            }
            extraItems.addAll(deltaRepository.findExtraItems(currentWeekStart));
        }
        recompute();
    }

    public void setStrategy(GroceryListStrategy strategy) {
        // Strategy swap
        this.strategy = strategy;
        recompute();
    }

    public List<GroceryItem> getGroceryList() {
        return groceryList;
    }

    public void addExtraItem(Ingredient ingredient) {
        extraItems.add(ingredient);
        deltaRepository.addExtraItem(currentWeekStart, ingredient);
        recompute();
    }

    public void removeExtraItem(Ingredient ingredient) {
        extraItems.remove(ingredient);
        deltaRepository.removeExtraItem(currentWeekStart, ingredient);
        recompute();
    }

    public void setBought(String name, String unit, boolean bought) {
        Key key = Key.from(name, unit);
        if (bought) {
            boughtKeys.add(key);
        } else {
            boughtKeys.remove(key);
        }
        deltaRepository.setBought(currentWeekStart, key.asBoughtKey(), bought);
        recompute();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void recompute() {
        // rebuild list + fire change if it actually changed
        List<GroceryItem> oldList = this.groceryList;
        List<Ingredient> combined = strategy.combine(flattenIngredients(currentMealPlan, extraItems));
        List<GroceryItem> newList = combined.stream()
                .map(ingredient -> new GroceryItem(ingredient, boughtKeys.contains(Key.from(ingredient))))
                .toList();
        this.groceryList = List.copyOf(newList);

        if (!oldList.equals(this.groceryList)) {
            propertyChangeSupport.firePropertyChange(PROP_GROCERY_LIST, oldList, this.groceryList);
        }
    }

    private static List<Ingredient> flattenIngredients(MealPlan plan, List<Ingredient> extras) {
        // flatten all recipe ingredients + extras into one list
        List<Ingredient> ingredients = new ArrayList<>();
        for (List<Recipe> recipes : plan.meals().values()) {
            for (Recipe recipe : recipes) {
                ingredients.addAll(recipe.ingredients());
            }
        }
        ingredients.addAll(extras);
        return ingredients;
    }

    private record Key(String nameKey, Dimension dimension) {
        static Key from(Ingredient ingredient) {
            return from(ingredient.name(), ingredient.unit());
        }

        static Key from(GroceryDeltaRepository.BoughtKey key) {
            return new Key(key.nameKey(), Dimension.valueOf(key.dimension()));
        }

        static Key from(String name, String unit) {
            String nameKey = normalizeName(name).toLowerCase();
            Dimension dimension = Unit.parse(unit).dimension();
            return new Key(nameKey, dimension);
        }

        GroceryDeltaRepository.BoughtKey asBoughtKey() {
            return new GroceryDeltaRepository.BoughtKey(nameKey, dimension.name());
        }
    }

    private static String normalizeName(String name) {
        return name.trim();
    }

    private static LocalDate weekStart(MealPlan mealPlan) {
        return mealPlan.date().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
}
