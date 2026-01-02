package be.uantwerpen.sd.project.viewfx;

import be.uantwerpen.sd.project.model.Ingredient;
import be.uantwerpen.sd.project.model.MealType;
import be.uantwerpen.sd.project.model.Recipe;
import be.uantwerpen.sd.project.model.grocery.GroceryItem;
import be.uantwerpen.sd.project.view.MealPlannerViewLogic;
import be.uantwerpen.sd.project.view.RenderPort;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MealPlannerView extends BorderPane implements RenderPort {
    private final ObservableList<Recipe> recipes = FXCollections.observableArrayList();
    private final ObservableList<GroceryItem> groceryItems = FXCollections.observableArrayList();

    private final ListView<Recipe> recipeList = new ListView<>(recipes);
    private final DatePicker weekPicker = new DatePicker(LocalDate.now());
    private final Label weekLabel = new Label();
    private final Label statusLabel = new Label();

    private LocalDate currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    private final List<Label> dayHeaders = new ArrayList<>();
    private final Map<MealType, List<ObservableList<Recipe>>> slotItemsByMealType = new EnumMap<>(MealType.class);
    private boolean updatingWeek = false;

    private final ToggleGroup unitToggleGroup = new ToggleGroup();
    private final RadioButton metric = new RadioButton("Metric");
    private final RadioButton imperial = new RadioButton("Imperial");

    private final ListView<GroceryItem> groceryList = new ListView<>(groceryItems);

    private final TextField recipeTitleField = new TextField();
    private final TextField recipeDescriptionField = new TextField();
    private final TextField recipeTagsField = new TextField();
    private final VBox ingredientRowsBox = new VBox(6);

    private final TextField extraName = new TextField();
    private final TextField extraAmount = new TextField();
    private final ComboBox<String> extraUnit = new ComboBox<>();

    private final Button addRecipeButton = new Button("Add Recipe");
    private final Button updateRecipeButton = new Button("Update Recipe");
    private final Button deleteRecipeButton = new Button("Delete Recipe");
    private final Button newRecipeButton = new Button("New Recipe");
    private Recipe selectedRecipe;

    private MealPlannerViewLogic logic;

    public MealPlannerView() {
        setPadding(new Insets(10));

        setLeft(buildRecipesPane());
        setCenter(buildMealPlanPane());
        setRight(buildGroceryPane());

        updateWeekHeaderLabels(currentWeekStart);
    }

    public void attachLogic(MealPlannerViewLogic logic) {
        this.logic = logic;
    }

    private VBox buildRecipesPane() {
        Label title = new Label("Recipes");

        recipeList.setPrefWidth(340);
        recipeList.setCellFactory(ignored -> new RecipeDragCell());
        recipeList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedRecipe = newVal;
            if (selectedRecipe != null) {
                loadRecipeIntoForm(selectedRecipe);
            }
            updateRecipeButtons();
        });

        Label addTitle = new Label("Add Recipe");
        recipeTitleField.setPromptText("Title");
        recipeDescriptionField.setPromptText("Description");
        recipeTagsField.setPromptText("Tags (comma separated, e.g. quick,italian)");

        ingredientRowsBox.getChildren().setAll(
                new IngredientRow(),
                new IngredientRow());

        Button addIngredientRow = new Button("Add Ingredient");
        addIngredientRow.setOnAction(evt -> ingredientRowsBox.getChildren().add(new IngredientRow()));

        addRecipeButton.setOnAction(evt -> {
            String titleText = recipeTitleField.getText().trim();
            logic.onAddRecipe(titleText, recipeDescriptionField.getText(), recipeTagsField.getText(), collectIngredients());
        });

        updateRecipeButton.setOnAction(evt -> {
            logic.onUpdateRecipe(
                    selectedRecipe.id(),
                    recipeTitleField.getText(),
                    recipeDescriptionField.getText(),
                    recipeTagsField.getText(),
                    collectIngredients());
        });

        deleteRecipeButton.setOnAction(evt -> {
            logic.onDeleteRecipe(selectedRecipe.id());
            clearRecipeInputs();
            recipeList.getSelectionModel().clearSelection();
        });

        newRecipeButton.setOnAction(evt -> {
            clearRecipeInputs();
            recipeList.getSelectionModel().clearSelection();
        });

        updateRecipeButtons();

        VBox pane = new VBox(8, title, recipeList, addTitle, recipeTitleField, recipeDescriptionField, recipeTagsField,
                new Label("Ingredients"), ingredientRowsBox, addIngredientRow,
                new HBox(8, addRecipeButton, updateRecipeButton, deleteRecipeButton, newRecipeButton));
        VBox.setVgrow(recipeList, Priority.ALWAYS);
        pane.setPadding(new Insets(0, 10, 0, 0));
        return pane;
    }

    private VBox buildMealPlanPane() {
        Label title = new Label("Weekly Meal Plan");

        weekLabel.setStyle("-fx-font-weight: bold;");

        weekPicker.setValue(LocalDate.now());
        weekPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            logic.onWeekSelected(newDate);
        });

        Button saveWeek = new Button("Save Week");
        saveWeek.setOnAction(evt -> {
            logic.onSaveWeek();
        });

        statusLabel.setStyle("-fx-opacity: 0.75;");
        VBox weekControls = new VBox(6,
                new HBox(10, new Label("Week:"), weekPicker, saveWeek),
                weekLabel,
                statusLabel);
        weekControls.setPadding(new Insets(0, 0, 6, 0));

        GridPane grid = buildWeekGrid();
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);

        VBox pane = new VBox(10, title, weekControls, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        pane.setPadding(new Insets(0, 12, 0, 12));
        return pane;
    }

    private GridPane buildWeekGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(6, 6, 6, 0));

        grid.getColumnConstraints().clear();
        ColumnConstraints dayCol = new ColumnConstraints();
        dayCol.setMinWidth(110);
        dayCol.setPrefWidth(110);
        grid.getColumnConstraints().add(dayCol);

        for (int i = 0; i < MealType.values().length; i++) {
            ColumnConstraints slotCol = new ColumnConstraints();
            slotCol.setMinWidth(220);
            slotCol.setPrefWidth(260);
            slotCol.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(slotCol);
        }

        dayHeaders.clear();
        slotItemsByMealType.clear();
        for (MealType type : MealType.values()) {
            slotItemsByMealType.put(type, new ArrayList<>());
        }

        grid.add(new Label("Day"), 0, 0);
        int col = 1;
        for (MealType mealType : MealType.values()) {
            Label header = new Label(mealType.name());
            header.setStyle("-fx-font-weight: bold;");
            grid.add(header, col++, 0);
        }

        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            Label dayLabel = new Label();
            dayLabel.setWrapText(true);
            dayHeaders.add(dayLabel);
            grid.add(dayLabel, 0, 1 + dayOffset);

            int mealCol = 1;
            for (MealType mealType : MealType.values()) {
                ObservableList<Recipe> items = FXCollections.observableArrayList();
                slotItemsByMealType.get(mealType).add(items);

                ListView<Recipe> slot = new ListView<>(items);
                slot.setFixedCellSize(32);
                slot.setPrefHeight(64);
                slot.setMinHeight(64);
                slot.setMaxHeight(64);
                slot.setMinWidth(220);
                slot.setPrefWidth(260);
                slot.setMaxWidth(Double.MAX_VALUE);
                slot.setPlaceholder(dimPlaceholder("Drop here"));
                slot.setCellFactory(ignored -> new ListCell<>() {
                    @Override
                    protected void updateItem(Recipe item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty || item == null ? null : item.title());
                    }
                });

                installDropTarget(slot, dayOffset, mealType);
                installSlotBehavior(slot, dayOffset, mealType, items);

                GridPane.setHgrow(slot, Priority.ALWAYS);
                grid.add(slot, mealCol++, 1 + dayOffset);
            }
        }

        return grid;
    }

    private void installDropTarget(ListView<Recipe> slot, int dayOffset, MealType mealType) {
        slot.setOnDragOver(evt -> {
            if (evt.getGestureSource() == slot) {
                return;
            }
            Dragboard db = evt.getDragboard();
            if (db.hasString() && db.getString().startsWith("recipeId:")) {
                evt.acceptTransferModes(TransferMode.COPY);
            }
            evt.consume();
        });

        slot.setOnDragDropped(evt -> handleDrop(evt, slot, dayOffset, mealType));
    }

    private void handleDrop(DragEvent evt, ListView<Recipe> slot, int dayOffset, MealType mealType) {
        boolean success = false;
        Dragboard db = evt.getDragboard();
        if (db.hasString() && db.getString().startsWith("recipeId:")) {
            String idRaw = db.getString().substring("recipeId:".length());
            long id = Long.parseLong(idRaw);
            Recipe recipe = findRecipeById(id);
            LocalDate date = currentWeekStart.plusDays(dayOffset);
            logic.onSetRecipeForSlot(date, mealType, recipe);
            success = true;
        }
        evt.setDropCompleted(success);
        evt.consume();
    }

    private Recipe findRecipeById(long id) {
        for (Recipe recipe : recipes) {
            if (recipe.id() == id) {
                return recipe;
            }
        }
        return null;
    }

    private void installSlotBehavior(ListView<Recipe> slot, int dayOffset, MealType mealType, ObservableList<Recipe> items) {
        items.addListener((ListChangeListener<Recipe>) change -> updateSlotStyle(slot, items));
        updateSlotStyle(slot, items);

        slot.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                LocalDate date = currentWeekStart.plusDays(dayOffset);
                logic.onSetRecipeForSlot(date, mealType, null);
            }
        });
    }

    private void updateSlotStyle(ListView<Recipe> slot, ObservableList<Recipe> items) {
        if (items.isEmpty()) {
            slot.setStyle("-fx-background-color: #f3f3f3; -fx-border-color: #d0d0d0; -fx-border-radius: 4; -fx-background-radius: 4;");
        } else {
            slot.setStyle("-fx-background-color: white; -fx-border-color: #8ab4f8; -fx-border-radius: 4; -fx-background-radius: 4;");
        }
    }

    private VBox buildGroceryPane() {
        Label title = new Label("Grocery List");

        metric.setToggleGroup(unitToggleGroup);
        metric.setSelected(true);
        imperial.setToggleGroup(unitToggleGroup);

        metric.setOnAction(evt -> {
            logic.onSelectMetric();
        });
        imperial.setOnAction(evt -> {
            logic.onSelectImperial();
        });

        groceryList.setPrefWidth(340);
        groceryList.setCellFactory(ignored -> new GroceryItemCell());

        Label extraTitle = new Label("Add Extra Item");
        extraName.setPromptText("Name");
        extraAmount.setPromptText("Amount");
        extraUnit.setItems(FXCollections.observableArrayList("g", "kg", "ml", "l", "oz", "lb", "fl oz"));
        extraUnit.setEditable(false);
        extraUnit.getSelectionModel().selectFirst();

        Button addExtra = new Button("Add Extra");
        addExtra.setOnAction(evt -> {
            String unit = extraUnit.getValue();
            logic.onAddExtraItem(extraName.getText(), extraAmount.getText(), unit);
        });

        GridPane extraGrid = new GridPane();
        extraGrid.setHgap(6);
        extraGrid.setVgap(6);
        extraGrid.addRow(0, new Label("Name"), extraName);
        extraGrid.addRow(1, new Label("Amount"), extraAmount);
        extraGrid.addRow(2, new Label("Unit"), extraUnit);

        VBox pane = new VBox(8, title, new HBox(10, metric, imperial), groceryList, extraTitle, extraGrid, addExtra);
        VBox.setVgrow(groceryList, Priority.ALWAYS);
        pane.setPadding(new Insets(0, 0, 0, 10));
        return pane;
    }

    @Override
    public void showRecipes(List<Recipe> recipes) {
        Platform.runLater(() -> this.recipes.setAll(recipes));
    }

    @Override
    public void showWeek(LocalDate weekStart, Map<LocalDate, Map<MealType, List<Recipe>>> mealsByDay) {
        Platform.runLater(() -> {
            updatingWeek = true;
            currentWeekStart = weekStart;
            updateWeekHeaderLabels(currentWeekStart);

            for (MealType type : MealType.values()) {
                List<ObservableList<Recipe>> daySlots = slotItemsByMealType.get(type);
                for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
                    LocalDate date = currentWeekStart.plusDays(dayOffset);
                    List<Recipe> items = mealsByDay.getOrDefault(date, Map.of()).getOrDefault(type, List.of());
                    daySlots.get(dayOffset).setAll(items);
                }
            }
            updatingWeek = false;
        });
    }

    @Override
    public void showGroceryList(List<GroceryItem> items) {
        Platform.runLater(() -> groceryItems.setAll(items));
    }

    @Override
    public void clearRecipeInputs() {
        Platform.runLater(() -> {
            recipeTitleField.clear();
            recipeDescriptionField.clear();
            recipeTagsField.clear();
            ingredientRowsBox.getChildren().setAll(new IngredientRow(), new IngredientRow());
            selectedRecipe = null;
            updateRecipeButtons();
        });
    }

    @Override
    public void clearExtraItemInputs() {
        Platform.runLater(() -> {
            extraName.clear();
            extraAmount.clear();
            extraUnit.getSelectionModel().selectFirst();
        });
    }

    @Override
    public void showStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    @Override
    public void showError(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, message).showAndWait());
    }

    private void updateWeekHeaderLabels(LocalDate weekStart) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE dd/MM");
        for (int dayOffset = 0; dayOffset < dayHeaders.size(); dayOffset++) {
            LocalDate date = weekStart.plusDays(dayOffset);
            dayHeaders.get(dayOffset).setText(fmt.format(date));
        }
        weekLabel.setText("Week of " + weekStart + " (Mon–Sun)");
    }

    private List<Ingredient> collectIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (javafx.scene.Node node : ingredientRowsBox.getChildren()) {
            if (!(node instanceof IngredientRow row)) {
                continue;
            }
            Ingredient ingredient = row.toIngredient();
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    private final class IngredientRow extends HBox {
        private final TextField name = new TextField();
        private final TextField amount = new TextField();
        private final ComboBox<String> unit = new ComboBox<>();

        private IngredientRow() {
            super(6);
            name.setPromptText("Name");
            amount.setPromptText("Amount");
            unit.setItems(FXCollections.observableArrayList("g", "kg", "ml", "l", "oz", "lb", "fl oz"));
            unit.getSelectionModel().selectFirst();

            Button remove = new Button("–");
            remove.setOnAction(evt -> ingredientRowsBox.getChildren().remove(this));

            HBox.setHgrow(name, Priority.ALWAYS);
            getChildren().addAll(name, amount, unit, remove);
        }

        private void setFromIngredient(Ingredient ingredient) {
            name.setText(ingredient.name());
            amount.setText(String.valueOf(ingredient.amount()));
            unit.getSelectionModel().select(ingredient.unit());
        }

        private Ingredient toIngredient() {
            String ingredientName = name.getText().trim();
            double value = Double.parseDouble(amount.getText().trim());
            String unitValue = unit.getValue();
            return new Ingredient(ingredientName, value, unitValue);
        }
    }

    private void updateRecipeButtons() {
        boolean hasSelection = selectedRecipe != null;
        addRecipeButton.setDisable(hasSelection);
        updateRecipeButton.setDisable(!hasSelection);
        deleteRecipeButton.setDisable(!hasSelection);
    }

    private void loadRecipeIntoForm(Recipe recipe) {
        recipeTitleField.setText(recipe.title());
        recipeDescriptionField.setText(recipe.description());
        recipeTagsField.setText(String.join(",", recipe.tags()));

        ingredientRowsBox.getChildren().clear();
        for (Ingredient ingredient : recipe.ingredients()) {
            IngredientRow row = new IngredientRow();
            row.setFromIngredient(ingredient);
            ingredientRowsBox.getChildren().add(row);
        }
        ingredientRowsBox.getChildren().add(new IngredientRow());
    }

    private final class RecipeDragCell extends ListCell<Recipe> {
        @Override
        protected void updateItem(Recipe item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.title());
            if (empty || item == null) {
                setOnDragDetected(null);
                return;
            }
            setOnDragDetected(evt -> {
                Dragboard db = startDragAndDrop(TransferMode.COPY);
                ClipboardContent content = new ClipboardContent();
                content.putString("recipeId:" + item.id());
                db.setContent(content);
                evt.consume();
            });
        }
    }

    private final class GroceryItemCell extends ListCell<GroceryItem> {
        private final javafx.scene.control.CheckBox checkBox = new javafx.scene.control.CheckBox();
        private final Label label = new Label();
        private final HBox row = new HBox(8, checkBox, label);
        private final BooleanProperty updating = new SimpleBooleanProperty(false);

        private GroceryItemCell() {
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (updating.get()) {
                    return;
                }
                GroceryItem item = getItem();
                Ingredient ingredient = item.ingredient();
                logic.onSetBought(ingredient.name(), ingredient.unit(), isSelected);
            });
            setGraphic(row);
        }

        @Override
        protected void updateItem(GroceryItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            updating.set(true);
            checkBox.setSelected(item.bought());
            Ingredient ingredient = item.ingredient();
            label.setText(ingredient.name() + " — " + ingredient.amount() + " " + ingredient.unit());
            setGraphic(row);
            updating.set(false);
        }
    }

    private static Label dimPlaceholder(String text) {
        Label label = new Label(text);
        label.setOpacity(0.35);
        label.setStyle("-fx-font-style: italic;");
        return label;
    }
}
