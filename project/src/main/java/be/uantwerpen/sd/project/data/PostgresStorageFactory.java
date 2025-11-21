package be.uantwerpen.sd.project.data;

public class PostgresStorageFactory extends StorageFactory {
    @Override
    public RecipeRepository getRecipeRepository() {
        return new PostgresRecipeRepository();
    }
}
