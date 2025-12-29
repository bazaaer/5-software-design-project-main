# AI usage statement — Smart Meal Planner project  
**Team:** Alec Boucham & Lander Van der Stighelen

## Why we used AI
We used generative AI mainly as a **learning accelerator** and a **pair-programming tutor**, not as a replacement for understanding.  
Because we are both *schakelstudenten* (coming from a different background), we relied on AI for fast feedback on unfamiliar topics (UML rules, JavaFX specifics, SQL syntax), while still validating everything against course material, documentation, and testing.

## How we used AI responsibly
- **Tutor-style usage:** ask “why” questions, request alternative approaches, and get rough sketches (e.g., what a diagram should contain).  
- **Verification & iteration:** we cross-checked suggestions with our own code/diagrams, official docs, and runtime/unit tests.  
- **No blind copy-paste:** AI output was adapted and reviewed; when AI was wrong (hallucinations or misinterpreting diagrams), we corrected it based on the actual requirements and our project structure.

---

## 1) Alec — UML & design diagrams
I used AI heavily at the start to speed up learning UML and Visual Paradigm diagramming, especially when building **class diagrams** and understanding relationships (association vs. aggregation/composition, etc.).  

My workflow:
1. Ask AI for a **rough textual sketch** of what the diagram should contain.
2. Create the diagram myself in Visual Paradigm based on that sketch.
3. After a few iterations, I started doing my own sketch first and only then compared it to AI suggestions.

AI was used as a **teacher**, similar to how an instructor might solve a few exercises together with you so you can “connect the dots”.  
I’m aware AI can hallucinate or misread diagram inputs, so I always verified the output against the actual requirements and the code.

### Resources I used
- AI: ChatGPT (GPT‑5.1 / GPT‑5.2, extended thinking mode)
- YouTube:
  - https://www.youtube.com/watch?v=PiRsKDlZeVk  
  - https://www.youtube.com/watch?v=sQgoFjxSdxo  
  - https://www.youtube.com/watch?v=WnMQ8HlmeXc  
  - https://www.youtube.com/watch?v=iLsJ0Ix_dho  
- Websites:
  - https://www.geeksforgeeks.org/system-design/unified-modeling-language-uml-class-diagrams/  
  - https://blog.visual-paradigm.com/step-by-step-class-diagram-tutorial-using-visual-paradigm/  
  - https://blog.visual-paradigm.com/use-case-diagram-tutorial/  

---

## 2) Lander — database/SQL, JavaFX help, and late-stage bug fixing

### 2.1 SQL generation (database + schema changes)
We pushed a bit beyond the basic requirements by adding database support. I considered patterns to keep the app compatible with multiple databases and consulted AI on whether **Abstract Factory** or **Factory Method** was the better fit for our case. I chose **Abstract Factory** because each database can provide the same “family” of repository objects, and I implemented the factory structure myself.

I wanted Postgres, but also wanted the app to run without extra dependencies, so we used a **file-based H2 database**.  
AI was used to speed up writing and iterating on:
- SQL schema scripts
- schema changes
- repository queries

SQL was not the main learning goal of the project, so AI helped me move faster, but I still verified by reading the schema and testing queries.

### 2.2 JavaFX UI support
I used AI for parts of the JavaFX UI that weren’t covered in lectures (e.g., adding a date selector or draggable UI elements). This helped me discover the right APIs and implementation patterns faster.

### 2.3 Bug fixing (grocery list persistence + cascade delete)
Near the end, I used AI to help debug a persistence issue:
- The grocery list is generated automatically from the meal plan (no “Generate grocery list” button).
- The UI updates through an observer-like mechanism using Java’s property change system (e.g., `GroceryListService` fires a `"groceryList"` change event; `MealPlannerViewLogic` listens and refreshes the UI).
- User changes (checked items and extra items) were initially kept only in memory and therefore got lost when reopening or switching weeks.

To fix this, I used an AI-suggested **delta approach**: store only user changes (bought state + extra items) in extra database tables and apply those deltas on top of the generated grocery list when a week is opened.

AI also suggested fixing referential issues with **`ON DELETE CASCADE`** so that deleting entities like recipes wouldn’t leave orphaned references.

---

## Summary
Across the project, we used AI mainly for:
- learning UML faster and validating diagram structure,
- quickly iterating on SQL and database wiring,
- discovering JavaFX UI patterns,
- troubleshooting a late-stage persistence bug and improving deletion consistency.

In all cases, we validated AI suggestions through documentation, code review, and testing.
