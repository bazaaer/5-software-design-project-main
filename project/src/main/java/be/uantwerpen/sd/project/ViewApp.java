package be.uantwerpen.sd.project;

import be.uantwerpen.sd.project.controller.MealPlannerController;
import be.uantwerpen.sd.project.model.grocery.GroceryListService;
import be.uantwerpen.sd.project.model.grocery.MetricGroceryListStrategy;
import be.uantwerpen.sd.project.model.storage.StorageFactory;
import be.uantwerpen.sd.project.view.MealPlannerViewLogic;
import be.uantwerpen.sd.project.viewfx.MealPlannerView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        StorageFactory storageFactory = StorageFactory.getInstance();

        GroceryListService model = new GroceryListService(
                new MetricGroceryListStrategy(),
                storageFactory.getGroceryDeltaRepository());
        MealPlannerController controller = new MealPlannerController(storageFactory, model);

        MealPlannerView view = new MealPlannerView();
        MealPlannerViewLogic logic = new MealPlannerViewLogic(controller, view);
        view.attachLogic(logic);

        stage.setTitle("Meal Planner — MVC (JavaFX)");
        stage.setScene(new Scene(view, 1400, 850));
        stage.setMaximized(true);
        stage.show();
        Platform.runLater(() -> stage.setMaximized(true));
    }
}
