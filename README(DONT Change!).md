# AI usage document

## Inhoudstabel
Lander Van der Stighelen

1. [SQL generation (database + schema changes)](#1-sql-generation-database--schema-changes)
2. [JavaFX UI](#2-javafx-ui)
3. [Bug fixing (grocery list persistence + cascade delete)](#3-bug-fixing-grocery-list-persistence--cascade-delete)

Alec Boucham

4. [Diagrams](#4-diagrams)
5. [Test Creation](#5-test-creation)
6. [Readme Creation](#6-readme-creation)
7. [Added info Alec](#7-extra-ref-from-alec-boucham)

We’re both "schakelstudenten" so we already had some experience working in groups on bigger projects, so we pushed a bit further than the basic requirements. That is why we started the project with the idea of adding a database. I used generative AI in a few places. Mainly for parts that were not the main focus of the course, and for solving a bug at the end.

## 1) SQL generation (database + schema changes)

I had the ideo of using a abstract factory or factory method to make the app compatible with multiple databases. To create this structure I consulted AI on which template is more appopriate: abstract factory or factory method.

I chose Abstract Factory because in this case u would have a different factory for each database that makes the "products", which really are the objects to store the data. Abstract Factory fits specifically well because you want each database to provide the same "family" of related repository objects. I then implemented the factory structure in code myself.

I wanted to use postgres database, but I also wanted the app to run without extra dependencies. That is where the file based H2 database comes in. I also used AI to generate and adjust the SQL scripts for the database, and writing the queries used by the repositories. SQL was not the main learning goal for this project, so AI helped me iterate faster. I still checked the code by reading the SQL schema and testing.

## 2) JavaFX UI

I used AI while building parts of the JavaFX UI, because that was not coverered in the lectures. With AI I was able to add fun stuff like a date selector and draggable objects.

## 3) Bug fixing (grocery list persistence + cascade delete)

Lastly I used AI right at the end of the project because I came across a very annoying bug. The grocery list is generated automatically from the current meal plan, and there is no “Generate grocery list” button. I also did not save complete grocery lists to the database, because they are generated automatically and updated very frequently. Instead, the UI updates the grocery list through an observer setup using Java’s property change mechanism: `GroceryListService` recalculates the grocery list when its inputs change and then fires a `"groceryList"` change event, and `MealPlannerViewLogic` listens for that event and refreshes the UI. This is why the grocery list is always updated immediately while you are planning meals.

Grocery list changes were not saved to the database and were only kept locally in memory, which meant they looked correct in the UI while the app was running. This means that checking the box on the grocery list or adding an item was not permanently saved, and the changes were also lost when navigating to a new week and coming back, because the grocery list was regenerated and the user changes were not loaded from anywhere.

In `MealPlannerViewLogic.java` there is also an in-memory structure for the UI flow: `private final Map<LocalDate, Map<MealType, Recipe>> plannedWeek = new HashMap<>();`. That structure stores the planned recipes per day and meal type and is written to the database after pressing Save Week, but it does not persist grocery list edits. To fix the grocery list persistence bug, I used generative AI to add the delta difference approach. This means I store only the user changes (bought state and extra items) in extra database tables, and I apply those deltas on top of the generated grocery list when a week is opened. That also needed an extra table in the database, which means more SQL.

I also fixed a bug by suggestion of AI where some deletions of items don't cascade. For example if you delete a recipe, the recipe can still be referenced in the meal plans. This was an easy fix by adding the `ON DELETE CASCADE` feature to the SQL script of the database, so related references are removed automatically. This does mean the app is dependent on a relational database if you would want to add more database options in the future by making the abstract factory pattern we implemented, but I don’t think that is a bad thing. I’m sure something like MongoDB also has options to cascade deletions, but it would typically be handled in application logic instead of through SQL constraints.

## 4) Diagrams

For the diagrams i used AI a lot to speed up the learning-procces, i (Alec) learn best when i can make the requested thing.
Meaning before i can start making things from the ground up, i need a lot of exercises where the awnser is partly given, as i can then connect the dots.

So the AI helped me by giving the rough sketch in text-form of how it should look like, then i realized the diagrams in VP.
A good example is all the class diagrams i needed to make, the first i relied heavily on AI to connect the classes, but after a few i started to get the hang of it.
I started to make the rough sketch myself, only then did i check what the AI would suggest.

## 5) Test Creation

During test creation i used AI in the same fashion as with the diagrams, the unit tests where created i the same style.
The Integration test was created in an substitute fashion, meaning that i described what i wanted and layed out the plan.
Then i let the ai give me hints to get me on track, seeing this is my first time writing java this was a very deep pool to jump in.

## 6) Readme Creation

The readme was made in the following fashion;
- Parsing the original ReadMe
- Adding the notes from both team-members
- Letting AI put it in an nice to read .md document
- Proof reading the output and make some minor adjustments

So in short Ai was mostly used to format the fragmented notes into a cohisive end result which is easy to read and browse.

## 7) Extra Ref from Alec Boucham

During this proccess the AI is used as a teacher, in the same way a teacher would also solve a few exercises with the student to show them how it's done.
This made it possible for me to ask questions about specific rules in UML or for example; why a class is an association and not an aggregation.

In short the goal of my AI usage is to get a better/quicker understanding of the required subjects, this in combination with sources from youtube listed below.
With this approach i try to leverage both the recent development in AI and the abundance of Youtube-videos available.

I am very aware of the possible hallucinations issues that AI-tools have, i have expirienced this first hand on an large number of occasions.
This is also why i always verify the output that is given with the input that was given, because the abilitiy to interpret images (like the VP-diagrams) is very limited. Just copy pasting the diagram (either as a XML/project or as image) is not going to get you where you want, it misinterprets a lot of connections between the classes.

In short, i use and used AI a lot throughout this course and the project, because i need a lot of continious feedback, which would be impossible in a regular class setting because of the limited time.

Below you can find most of the resouces that i used;

- AI -> ChatGPT(5.1 and 5.2) only the extended thinking mode.
- Youtube:
    - https://www.youtube.com/watch?v=PiRsKDlZeVk
    - https://www.youtube.com/watch?v=sQgoFjxSdxo
    - https://www.youtube.com/watch?v=WnMQ8HlmeXc
    - https://www.youtube.com/watch?v=iLsJ0Ix_dho
- Website's:
    - https://www.geeksforgeeks.org/system-design/unified-modeling-language-uml-class-diagrams/
    - https://blog.visual-paradigm.com/step-by-step-class-diagram-tutorial-using-visual-paradigm/
    - https://blog.visual-paradigm.com/use-case-diagram-tutorial/
