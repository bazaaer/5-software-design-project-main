// package be.uantwerpen.sd.project;

// import be.uantwerpen.sd.project.storage.RecipeRepository;
// import be.uantwerpen.sd.project.storage.StorageFactory;
// import be.uantwerpen.sd.project.data.Recipe;
// import java.util.ArrayList;
// import org.junit.jupiter.api.Test;

// import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
// import static org.junit.jupiter.api.Assertions.assertNotNull;

// public class StorageFactoryITest {

//     @Test
//     public void testFactoryCreationAndUsage() {
//         // 1. Ask the Abstract Factory for the correct instance
//         StorageFactory factory = StorageFactory.getInstance();
//         assertNotNull(factory, "Factory instance should not be null");
//         System.out.println("Factory Type: " + factory.getClass().getSimpleName());

//         // 2. Ask the factory for a Repository (The Product)
//         RecipeRepository repo = factory.getRecipeRepository();
//         assertNotNull(repo, "Repository instance should not be null");
//         System.out.println("Repository Type: " + repo.getClass().getSimpleName());

//         // 3. Use the repository (The Client Code)
//         Recipe recipe = new Recipe("Spaghetti Bolognese", "A classic Italian dish.", new ArrayList<>(),
//                 new ArrayList<>());
//         assertDoesNotThrow(() -> repo.save(recipe), "Repository should save recipe without exception");
//     }
// }
