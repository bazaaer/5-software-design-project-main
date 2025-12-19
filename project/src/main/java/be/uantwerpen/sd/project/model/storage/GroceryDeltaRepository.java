package be.uantwerpen.sd.project.model.storage;

import be.uantwerpen.sd.project.model.Ingredient;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface GroceryDeltaRepository {

    Set<BoughtKey> findBoughtKeys(LocalDate weekStart);

    void setBought(LocalDate weekStart, BoughtKey key, boolean bought);

    List<Ingredient> findExtraItems(LocalDate weekStart);

    void addExtraItem(LocalDate weekStart, Ingredient ingredient);

    void removeExtraItem(LocalDate weekStart, Ingredient ingredient);

    record BoughtKey(String nameKey, String dimension) {
    }
}

