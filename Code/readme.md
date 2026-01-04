# Smart Meal Planner

This README is intentionally short because the teachers already know the requirements. We only mention the extra work and where the required patterns live in the code.

## Extras We added

- Database persistence and the choice between Postgres or H2. The choice is set in `Code/.env` with `DB_TYPE=postgres` or `DB_TYPE=h2`.

## Required design patterns (where they are in code)

- MVC  
  - View: `Code/src/main/java/be/uantwerpen/sd/project/viewfx/MealPlannerView.java`  
  - Controller: `Code/src/main/java/be/uantwerpen/sd/project/controller/MealPlannerController.java`  
  - Model: `Code/src/main/java/be/uantwerpen/sd/project/model/...`

- Singleton (thread-safe)  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/storage/StorageFactory.java`  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/storage/h2/H2StorageFactory.java`  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/storage/postgres/PostgresStorageFactory.java`

- Observer (not the MVC one)  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/grocery/GroceryListService.java` fires property changes  
  - `Code/src/main/java/be/uantwerpen/sd/project/view/MealPlannerViewLogic.java` listens and refreshes the UI

- Abstract Factory (selects the storage implementation at runtime based on `DB_TYPE`)  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/storage/StorageFactory.java` selects the concrete factory  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/storage/h2/...` and `Code/src/main/java/be/uantwerpen/sd/project/model/storage/postgres/...` provide the concrete products

- Strategy (switches grocery list calculation between metric and imperial)  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/grocery/GroceryListStrategy.java`  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/grocery/MetricGroceryListStrategy.java`  
  - `Code/src/main/java/be/uantwerpen/sd/project/model/grocery/ImperialGroceryListStrategy.java`
