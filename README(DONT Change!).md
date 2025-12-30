# Smart Meal Planner & Grocery List (UAntwerpen – 5 Software Design)

This repository contains **our project** for the course **5–Software Design** at the **University of Antwerp**:  
a **Smart Meal Planner & Grocery List Generator**.

The application helps users to:
- manage recipes,
- create a weekly meal plan,
- automatically generate and maintain a grocery list (including manual extras and “checked” items),
- persist data using a pluggable storage layer (H2 implementation included).

---

## Table of Contents

- [1. Introduction](#1-introduction)
- [2. Project Overview](#2-project-overview)
    - [2.1 Goal](#21-goal)
    - [2.2 Team](#22-team)
- [3. Features](#3-features)
    - [3.1 Recipe Management](#31-recipe-management)
    - [3.2 Weekly Meal Planning](#32-weekly-meal-planning)
    - [3.3 Grocery List Generation + Deltas](#33-grocery-list-generation--deltas)
    - [3.4 Unit Systems (Metric / Imperial)](#34-unit-systems-metric--imperial)
    - [3.5 Persistence (H2 Storage)](#35-persistence-h2-storage)
- [4. Architecture & Design Patterns](#4-architecture--design-patterns)
    - [4.1 MVC Layers](#41-mvc-layers)
    - [4.2 Implemented Patterns](#42-implemented-patterns)
- [5. UML & Documentation](#5-uml--documentation)
- [6. Tests](#6-tests)
    - [6.1 Unit Tests](#61-unit-tests)
    - [6.2 Integration Tests](#62-integration-tests)
- [7. Technology & Running the Project](#7-technology--running-the-project)
    - [7.1 Requirements](#71-requirements)
    - [7.2 Build](#72-build)
    - [7.3 Run](#73-run)
    - [7.4 Run Tests](#74-run-tests)
- [8. Git Workflow](#8-git-workflow)

---

## 1. Introduction

This README describes **our concrete implementation** of the assignment option  
**“Smart Meal Planner & Grocery List Generator”** for *5–Software Design*.

It documents:
- what we built,
- how the project is structured,
- which patterns are used (and where),
- how data is stored,
- and how to run and test the application.

---

## 2. Project Overview

### 2.1 Goal

The goal of our Smart Meal Planner is to:

- plan meals for a full week (Monday → Sunday),
- manage recipes and ingredients in one place,
- generate a grocery list automatically from the plan,
- keep manual grocery changes stable (extras + checked items),
- deliver a clean, maintainable codebase with **required design patterns**, **UML**, and **tests**.

### 2.2 Team

- Student 1: Lander
- Student 2: Alec

Course: **5–Software Design** – FTI, University of Antwerp  
Academic year: **2025–2026**

---

## 3. Features

### 3.1 Recipe Management

Users maintain a **recipe library**.

A recipe contains:
- title + description,
- a list of ingredients (name, amount, unit),
- optional metadata (tags, servings, time, …) depending on our implementation.

Supported actions (via controller + view):
- add / edit / delete recipes,
- view recipe details.

### 3.2 Weekly Meal Planning

The meal planner supports a full **7-day week**.

Typical meal slots:
- breakfast, lunch, dinner, snacks  
  (Depending on the UI, some slots may be optional/configurable.)

Supported actions:
- schedule a recipe on a day + meal type,
- replace or remove scheduled recipes,
- view the complete weekly plan.

### 3.3 Grocery List Generation + Deltas

The grocery list is generated from the current meal plan:

- all ingredients from planned recipes are merged by name + unit system rules,
- quantities are summed,
- the list is recalculated automatically when the plan changes.

**Important project detail (update):**  
We do not want a recalculation to destroy user actions. Therefore we support **deltas**:
- user-added extra grocery items are preserved,
- “checked off / bought” states are preserved,
- these deltas are stored separately and re-applied after recomputation.

This results in a grocery list that stays consistent even when the meal plan changes.

### 3.4 Unit Systems (Metric / Imperial)

The grocery list supports different **unit strategies**:
- **Metric strategy** (e.g., g ↔ kg, ml ↔ l),
- **Imperial strategy** (e.g., oz ↔ lb, … depending on our implementation).

The exact conversion and rounding thresholds are implemented inside the strategy classes
(see the grocery list strategy implementations in the codebase).

### 3.5 Persistence (H2 Storage)

The project includes a **storage layer** with repository interfaces and an H2 implementation.

Data that can be persisted (depending on current build):
- recipes,
- meal plans,
- grocery deltas (extras + checked state),
- and related entities.

Storage is selected through a **StorageFactory** (see Architecture section).

---

## 4. Architecture & Design Patterns

### 4.1 MVC Layers

We use **Model–View–Controller (MVC)**:

- **Model**
    - domain classes like `Recipe`, `Ingredient`, `MealPlan`, `MealType`, grocery list entities, …
    - domain services like the grocery recompute logic
- **View**
    - UI layer (JavaFX in our project setup)
    - displays recipes, weekly plan, grocery list
- **Controller**
    - orchestrates use cases such as:
        - add/edit recipe
        - save/update meal plan
        - recompute grocery list
        - toggle metric/imperial strategy

### 4.2 Implemented Patterns

We implement the required patterns in a way that fits this domain:

- **Singleton**
    - used for a central/shared access point (e.g., configuration / storage selection / controller instance), depending on our implementation.
- **Observer**
    - grocery list updates automatically when meal plan changes (implemented through an explicit notification mechanism).
    - this keeps recomputation logic decoupled from UI actions.
- **Creational pattern (Factory)**
    - storage creation is abstracted behind a factory (`StorageFactory`) so repositories can be swapped without changing business logic.
- **Extra pattern: Strategy**
    - grocery list generation uses a `GroceryListStrategy` to support Metric vs Imperial behaviour.

**Also used (not required but relevant):**
- **Repository abstraction** for persistence boundaries (domain does not depend directly on H2/JDBC code).

---

## 5. UML & Documentation

We provide (as required by the assignment):
- a global class diagram,
- mini class diagrams per design pattern,
- a use case diagram,
- at least one sequence diagram for a key flow (e.g., “Save Meal Plan → Recompute Grocery List”).

> The UML files and exports are included in the repository as part of the deliverables (see project documentation folder).

---

## 6. Tests

### 6.1 Unit Tests

Unit tests focus on core logic such as:
- combining ingredients,
- rounding and unit conversion rules,
- empty inputs and edge cases,
- strategy-specific behaviour (Metric vs Imperial).

### 6.2 Integration Tests

Integration tests focus on realistic end-to-end flows through the controller + storage + domain logic, for example:
- create recipes → save meal plan → generate grocery list → apply deltas,
- verify persistence using an H2 test database setup.

> Integration tests are designed to be simple but meaningful, and should run automatically with `mvn test`.

---

## 7. Technology & Running the Project

### 7.1 Requirements

- **Java**
- **Maven**
- (If applicable) JavaFX configured via Maven dependencies/plugins (depending on your environment)

### 7.2 Build

```bash
mvn clean install
```

### 7.3 Run

If the project is configured with an executable main class:

```bash
mvn exec:java
```

If you run from an IDE:
- open the project as a Maven project
- run the main entry point from the `view` or `Main` class (depending on project setup)

### 7.4 Run Tests

```bash
mvn test
```

---

## 8. Git Workflow

We use Git with:
- feature branches for new work,
- meaningful commits,

---
