# Smart Meal Planner & Grocery List

This repository contains **our project** for the course **5–Software Design** at the University of Antwerp:  
a **Smart Meal Planner & Grocery List Generator**.

The application helps users to:
- manage recipes,
- create a weekly meal plan,
- automatically generate a grocery list based on that plan.

---

## Table of Contents

- [1. Introduction](#1-introduction)
- [2. Project Overview](#2-project-overview)
  - [2.1 Goal](#21-goal)
  - [2.2 Team](#22-team)
- [3. Features](#3-features)
  - [3.1 Manage Recipes](#31-manage-recipes)
  - [3.2 Weekly Planning](#32-weekly-planning)
  - [3.3 Automatic Grocery List](#33-automatic-grocery-list)
  - [3.4 Optional / Extra Features](#34-optional--extra-features)
- [4. Architecture & Design Patterns](#4-architecture--design-patterns)
- [5. UML & Documentation](#5-uml--documentation)
- [6. Tests](#6-tests)
- [7. Technology & Implementation](#7-technology--implementation)
- [8. Git Workflow](#8-git-workflow)
- [9. AI Usage](#9-ai-usage)
- [10. TODO List](#10-todo-list)
- [11. Credits](#11-credits)

---

## 1. Introduction

This document describes **our concrete project**, based on the option  
**“Smart Meal Planner & Grocery List Generator”** from the course *5–Software Design*.

Instead of focusing on the generic assignment, this README describes:
- what **we** are actually building,
- how the application is structured,
- which design patterns, UML diagrams, and tests we provide.

---

## 2. Project Overview

### 2.1 Goal

The goal of our Smart Meal Planner is to:

- make it easier to plan meals for a full week,
- manage recipes and ingredients in one place,
- automatically generate a grocery list so you don’t forget anything,
- provide a clear and maintainable codebase with correct design patterns.

The focus lies on both **functionality** and **software design** (patterns, UML, tests).

### 2.2 Team

- Student 1: _<Name + r-number>_  
- Student 2: _<Name + r-number>_  

Course: **5–Software Design** – FTI, University of Antwerp  
Academic year: **2025–2026**

---

## 3. Features

### 3.1 Manage Recipes

The application lets the user manage a personal **recipe library**.

Each **recipe** contains at least:

- Title  
- Description  
- List of ingredients (with **quantity** and **unit**)  
- Optional tags (e.g. `vegetarian`, `quick`, `budget`)  
- (Optional) number of servings, preparation time, difficulty level, …

The user can:

- **add** recipes,  
- **view** recipes (detail view),  
- **edit** recipes,  
- **delete** recipes,  
- (optional) search/filter by title, ingredient, or tag.

### 3.2 Weekly Planning

The app supports a **weekly meal plan for 7 days** (Monday to Sunday).

By default, we provide the following meal slots per day:

- Breakfast  
- Lunch  
- Dinner  
- Snacks  

The user can:

- **schedule** a recipe for each day and meal slot,  
- **view** the full weekly plan,  
- **change or remove** scheduled recipes,  
- (optional) configure custom meal slots (add/remove slots).

### 3.3 Automatic Grocery List

Based on the **current weekly meal plan**, the application generates an **automatic grocery list**:

- all ingredients of all scheduled recipes are combined;
- quantities per ingredient are **summed up**  
  - example: 2× `100 g pasta` → `200 g pasta`.

The grocery list is **automatically recalculated** whenever the planning changes.

In this list, the user can:

- **check off** items as “bought”,  
- manually **add** extra ad-hoc items (e.g. toilet paper, drinks, …),  
- (optional) **group** items per category (vegetables, dairy, dry goods, …).

### 3.4 Optional / Extra Features

Depending on available time, we may add these extra features:

- **Persistence**: store recipes, weekly plans, and the grocery list in a file or database;  
- **Diet preferences**: e.g. vegetarian, vegan, gluten-free, with visual indicators in the planner;  
- **Statistics**: most used recipes, most used ingredients, …;  
- **Template weeks**: save favorite weekly plans and reload them later.

---

## 4. Architecture & Design Patterns

We explicitly design the application around a number of design patterns, in line with the assignment requirements.

### MVC

We use **Model–View–Controller (MVC)** as the global architecture:

- **Model**  
  - domain classes such as `Recipe`, `Ingredient`, `MealSlot`, `MealPlan`, `GroceryList`, …
- **View**  
  - GUI components for recipe management, weekly planning, and grocery list display
- **Controller**  
  - handles user actions, updates the models, and triggers view updates

### Required patterns

We implement at least the following patterns:

- **Singleton (thread-safe)**  
  - e.g. for a central `AppConfig`, `RecipeRepository`, or `DataStore`.

- **Observer**  
  - used in the domain, e.g. a `MealPlan` or `RecipeRepository` that notifies observers when data changes,  
    so the grocery list and/or views can update automatically.  
  - This is **independent of** any Observer mechanisms that might exist in the MVC/GUI framework.

- **One creational pattern** (Factory Method, Abstract Factory, or Builder)  
  - e.g. a **Builder** for complex `Recipe` objects,  
  - or a **Factory** for standard weekly plans or meal slots.

### Extra pattern

We also implement **at least one extra pattern** that fits the domain logically, for example:

- **Strategy** – different strategies to generate a grocery list (per store, per category, per price, …);
- **Decorator** – dynamically add extra properties to recipes or grocery list items;
- **Command** – actions such as “schedule recipe” / “change planning” with undo/redo;
- others (Adapter, Façade, Proxy, Composite, State) if motivated.

Which patterns are actually used will be explained in the documentation and UML.

---

## 5. UML & Documentation

We provide the following UML diagrams:

1. **Global class diagram** of the whole application  
   - the GUI may be represented as a single `GUI`/`View` class if desired.

2. **Mini class diagrams per design pattern**  
   - showing only the classes and relationships forming the pattern.

3. **Use case diagram**  
   - with the main actor(s) (user)  
   - and the main use cases (manage recipes, create weekly plan, generate grocery list, …).

4. **At least one sequence diagram**  
   - e.g. for “Generate grocery list based on weekly plan”  
   - or “Schedule recipe for a specific day/meal slot”.

We try to use UML as a **living blueprint**: when the design changes, we update the diagrams.

---

## 6. Tests

We provide both **unit tests** and **integration tests**.

### Unit tests

- At least one **important class** (e.g. `GroceryListService`, `MealPlan`, `RecipeRepository`, …) gets extensive unit tests.
- We test, among other things:
  - correct summation of ingredients,
  - behaviour with an empty weekly plan,
  - duplicate ingredients,
  - invalid input or edge cases.

### Integration test(s)

- At least one **integration test** that runs through a realistic use case, for example:
  - create recipes → set up weekly plan → generate grocery list.
- This test uses at least controller + model (and optionally a simple view abstraction).

---

## 7. Technology & Implementation

> We will update this section once the project setup is complete.

- Programming language: **Java**  
- Build tool: **Maven** or **Gradle** (aligned with the labs)  
- Java version: **17** (or whatever the course requires)

### 7.1 Building and running the project

Example using Maven:

```bash
# Clone repository
git clone <REPO-URL>
cd <PROJECT-FOLDER>

# Build
mvn clean install

# Run application (adjust according to the real main class)
mvn exec:java
```

Useful links:

- Git: <https://git-scm.com/>  
- Git introduction (video): <https://www.youtube.com/watch?v=SWYqp7iY_Tc>  
- Java API documentation: <https://docs.oracle.com/en/java/javase/17/docs/api/>

---

## 8. Git Workflow

We use **Git** to collaborate and keep track of change history.

Agreements (proposal):

- use **feature branches** for new functionality,
- clear, meaningful commit messages,
- regular merge requests or pull requests (if using GitHub/GitLab),
- main/master should in principle always **compile** and **run tests**.

Some handy Git commands:

```bash
# Create new branch
git checkout -b feature/new-feature

# Add and commit changes
git add .
git commit -m "Describe clearly what changed"

# Push branch
git push origin feature/new-feature
```

More info: <https://git-scm.com/doc>

---

## 9. AI Usage

According to the course guidelines, we may use **generative AI** with some limitations:

- We only use AI to:
  - better understand syntax and tooling,
  - explore design patterns and architectural decisions,
  - analyse error messages or bugs.
- We must **understand and be able to explain** all final code and UML ourselves.
- We keep a **short log** of:
  - when AI was used,
  - for which problem,
  - and how we adapted or improved the output.

---

## 10. TODO List

Below we keep track of what still needs to be done.  
This is an **internal planning** for our team.

### General

- [ ] Complete README with real names, r-numbers, and repo URL  
- [x] Set up base project (package structure, main class, build-tool configuration)  
- [x] Align folder structure with labs (src/main/java, src/test/java, …)  

### Domain & logic

- [x] Design domain model (`Recipe`, `Ingredient`, `MealPlan`, `MealSlot`, `GroceryList`, …)  
- [x] First version of `RecipeRepository` + choose persistence strategy  
- [x] Implement logic for generating the grocery list  
- [x] Implement Observer link between planning and grocery list  

### Design patterns

- [x] Choose and implement Singleton class (e.g. config or datastore)  
- [x] Implement Observer interface and concrete observers  
- [x] Design and add a creational pattern (Factory/Builder/Abstract Factory)  
- [x] Choose and integrate an extra pattern (e.g. Strategy or Command)  

### GUI

- [x] Set up basic GUI (recipe overview, weekly planning, grocery list)  
- [x] Enforce MVC structure in code (Model, View, Controller)  
- [x] Fully implement use case “add recipe”  
- [x] Fully implement use case “create weekly plan + generate grocery list”  

### UML & documentation

- [x] Create global class diagram  
- [ ] Create mini class diagrams per pattern  
- [x] Draw use case diagram  
- [ ] Create at least one sequence diagram  
- [x] Update UML when major design changes occur  

### Tests

- [ ] Write unit tests for main domain classes  
- [ ] Set up integration test for a realistic use case  
- [ ] Check test coverage and extend where needed  

---

## 11. Credits

If we reuse code or ideas from external sources, we list them here.

Examples:

- StackOverflow answers for specific Java problems  
- Official documentation (Java, JUnit, Maven, …)  
- Tutorials or YouTube videos about design patterns or Java GUI

Format (example):

- “Builder pattern setup inspired by example from <link>”  
- “Git branching strategy based on blog post <link>”  

---

_End of README – version 1 (EN, project-specific)._