package be.uantwerpen.sd.project.data;

public class H2StorageFactory extends StorageFactory {
    @Override
    public RecipeRepository getRecipeRepository() {
        return new H2RecipeRepository();
    }
}
