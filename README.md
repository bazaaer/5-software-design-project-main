# Smart Meal Planner & Boodschappenlijst

Dit repository bevat **ons project** voor het vak **5–Software Design** aan de Universiteit Antwerpen:  
een **Smart Meal Planner & Boodschappenlijst Generator**.

De applicatie helpt gebruikers om:
- recepten te beheren,
- een weekmenu op te stellen,
- automatisch een boodschappenlijst te genereren op basis van dat menu.

---

## Inhoudstafel

- [1. Inleiding](#1-inleiding)
- [2. Projectoverzicht](#2-projectoverzicht)
  - [2.1 Doel](#21-doel)
  - [2.2 Team](#22-team)
- [3. Functionaliteiten](#3-functionaliteiten)
  - [3.1 Recepten beheren](#31-recepten-beheren)
  - [3.2 Weekplanning](#32-weekplanning)
  - [3.3 Automatische boodschappenlijst](#33-automatische-boodschappenlijst)
  - [3.4 Optionele / extra features](#34-optionele--extra-features)
- [4. Architectuur & Design Patterns](#4-architectuur--design-patterns)
- [5. UML & Documentatie](#5-uml--documentatie)
- [6. Tests](#6-tests)
- [7. Technologie & Uitvoering](#7-technologie--uitvoering)
- [8. Git Workflow](#8-git-workflow)
- [9. AI-gebruik](#9-ai-gebruik)
- [10. TODO-lijst](#10-todo-lijst)
- [11. Credits](#11-credits)

---

## 1. Inleiding

Dit document beschrijft **ons concrete project**, gebaseerd op de optie  
**“Smart Meal Planner & Grocery List Generator”** uit het vak *5–Software Design*.

In plaats van de algemene opdrachtfocus, beschrijft deze README:
- wat **wij** precies bouwen,
- hoe de applicatie is opgebouwd,
- welke design patterns, UML-diagrammen en tests we voorzien.

Voor algemene richtlijnen (deadlines, puntenverdeling, …) verwijzen we naar:
- de **PowerPoint** van het vak (op Blackboard),
- de **Blackboard-cursuspagina**: <https://blackboard.uantwerpen.be>

---

## 2. Projectoverzicht

### 2.1 Doel

Het doel van onze Smart Meal Planner is om:

- het plannen van maaltijden voor een volledige week eenvoudiger te maken,
- recepten en ingrediënten centraal te beheren,
- automatisch een boodschappenlijst te genereren zodat je niets vergeet,
- een duidelijke en onderhoudbare codebasis te maken met correcte design patterns.

De nadruk ligt zowel op **functionaliteit** als op **software-ontwerp** (patterns, UML, tests).

### 2.2 Team

- Student 1: _\<Naam + r-nummer\>_  
- Student 2: _\<Naam + r-nummer\>_  

Vak: **5–Software Design** – FTI, Universiteit Antwerpen  
Academiejaar: **2024–2025**

---

## 3. Functionaliteiten

### 3.1 Recepten beheren

De applicatie laat de gebruiker een persoonlijke **receptenbibliotheek** beheren.

Elke **recept** bevat minstens:

- Titel  
- Beschrijving  
- Lijst van ingrediënten (met **hoeveelheid** en **eenheid**)  
- Optionele tags (bv. `vegetarisch`, `snel`, `budget`)  
- (Optioneel) aantal porties, bereidingstijd, moeilijkheidsgraad, …

De gebruiker kan:

- recepten **toevoegen**,  
- recepten **bekijken** (detailweergave),  
- recepten **bewerken**,  
- recepten **verwijderen**,  
- (optioneel) zoeken/filteren op titel, ingrediënt of tag.

### 3.2 Weekplanning

De app ondersteunt een **weekmenu van 7 dagen** (maandag t.e.m. zondag).

Per dag voorzien we standaard de volgende maaltijd-slots:

- Ontbijt  
- Lunch  
- Avondmaal  
- Snacks  

De gebruiker kan:

- per dag en per slot een recept **inplannen**,  
- de volledige **weekplanning bekijken**,  
- geplande recepten **wijzigen of verwijderen**,  
- (optioneel) zelf maaltijd-slots configureren (slots toevoegen/verwijderen).

### 3.3 Automatische boodschappenlijst

Op basis van de **huidige weekplanning** genereert de applicatie een **boodschappenlijst**:

- alle ingrediënten van alle ingeplande recepten worden samengevoegd;
- hoeveelheden per ingrediënt worden **opgeteld**  
  - voorbeeld: 2× `100 g pasta` → `200 g pasta`.

De boodschappenlijst wordt **automatisch herberekend** wanneer de planning wijzigt.

In deze lijst kan de gebruiker:

- items **afvinken** als “gekocht”,  
- extra ad-hoc items **handmatig toevoegen** (bv. wc-papier, drank, …),  
- (optioneel) items laten **groeperen per categorie** (groenten, zuivel, droge voeding, …).

### 3.4 Optionele / extra features

Afhankelijk van de beschikbare tijd kunnen we volgende extra functies toevoegen:

- **Persistentie**: recepten, weekplannen en boodschappenlijst opslaan in een bestand of database;  
- **Dieetvoorkeuren**: bv. vegetarisch, vegan, glutenvrij, en visuele aanduiding in de planner;  
- **Statistieken**: meest gebruikte recepten, meest gebruikte ingrediënten, …;  
- **Template-weken**: favoriete weekplanningen bewaren en opnieuw inladen.

---

## 4. Architectuur & Design Patterns

We ontwerpen de applicatie expliciet rond een aantal design patterns, in lijn met de opdrachteisen.

### MVC

We gebruiken **Model–View–Controller (MVC)** als globale architectuur:

- **Model**  
  - domeinklassen zoals `Recipe`, `Ingredient`, `MealSlot`, `MealPlan`, `GroceryList`, …
- **View**  
  - GUI-componenten voor receptenbeheer, weekplanning en boodschappenlijstweergave
- **Controller**  
  - vangt gebruikersacties op, stuurt de modellen aan en laat de views hertekenen

### Verplichte patterns

We implementeren minstens de volgende patronen:

- **Singleton (thread-safe)**  
  - bv. voor een centrale `AppConfig`, `RecipeRepository` of `DataStore`.

- **Observer**  
  - gebruikt in het domein, bv. een `MealPlan` of `RecipeRepository` dat observers verwittigt wanneer data wijzigt,  
    zodat de boodschappenlijst en/of views automatisch kunnen updaten.  
  - Dit staat **los van** eventuele Observer-mechanismen die in de MVC/GUI zitten.

- **Één creational pattern** (Factory Method, Abstract Factory of Builder)  
  - bv. een **Builder** voor complexe `Recipe`-objecten,  
  - of een **Factory** voor standaard weekplanningen of meal slots.

### Extra patroon

Daarnaast implementeren we **minstens één extra patroon** dat logisch past in het domein, bijvoorbeeld:

- **Strategy** – verschillende strategieën om een boodschappenlijst te genereren (per winkel, per categorie, per prijs, …);
- **Decorator** – dynamisch extra eigenschappen toevoegen aan recepten of boodschappenlijst-items;
- **Command** – acties zoals “recept inplannen”/“planning wijzigen” met undo/redo;
- andere (Adapter, Façade, Proxy, Composite, State) indien gemotiveerd.

Welke patronen effectief gebruikt worden, lichten we toe in de documentatie en UML.

---

## 5. UML & Documentatie

We voorzien volgende UML-diagrammen:

1. **Globaal klassendiagram** van de hele applicatie  
   - de GUI kan eventueel als één klasse `GUI`/`View` voorgesteld worden.

2. **Mini-klassendiagram per design pattern**  
   - toont enkel de klassen en relaties die het patroon vormen.

3. **Use case diagram**  
   - met de belangrijkste actor(en) (gebruiker)  
   - en de belangrijkste use cases (recepten beheren, weekplan maken, boodschappenlijst genereren, …).

4. **Minstens één sequentiediagram**  
   - bv. voor “Boodschappenlijst genereren op basis van weekplan”  
   - of “Recept inplannen voor een bepaalde dag/maaltijd-slot”.

We proberen UML als een **levende blauwdruk** te gebruiken: bij wijzigingen in het ontwerp passen we de diagrammen aan.

---

## 6. Tests

We voorzien zowel **unit tests** als **integratietests**.

### Unit tests

- Minstens één **belangrijke klasse** (bv. `GroceryListService`, `MealPlan`, `RecipeRepository`, …) krijgt uitgebreide unit tests.
- We testen o.a.:
  - correcte optelling van ingrediënten,
  - gedrag bij lege weekplanning,
  - dubbele ingrediënten,
  - foutieve invoer of randgevallen.

### Integratietest(en)

- Minstens één **integratietest** die een realistische use case doorloopt, bijvoorbeeld:
  - aanmaken van recepten → opstellen weekplan → genereren boodschappenlijst.
- Deze test gebruikt minstens controller + model (en eventueel een eenvoudige view-abstractie).

---

## 7. Technologie & Uitvoering

> Dit gedeelte passen we aan zodra het project volledig is opgezet.

- Programmeertaal: **Java**  
- Build-tool: **Maven** of **Gradle** (in lijn met de labs)  
- Java-versie: **17** (of wat het vak voorschrijft)

### 7.1 Project bouwen en starten

Voorbeeld met Maven:

```bash
# Repository clonen
git clone <REPO-URL>
cd <PROJECT-FOLDER>

# Bouwen
mvn clean install

# Applicatie runnen (pas aan volgens de echte main-class)
mvn exec:java
```

Nuttige links:

- Git: <https://git-scm.com/>  
- Git-inleiding (video): <https://www.youtube.com/watch?v=SWYqp7iY_Tc>  
- Java API-documentatie: <https://docs.oracle.com/en/java/javase/17/docs/api/>

---

## 8. Git Workflow

We gebruiken **Git** om samen te werken en wijzigingsgeschiedenis bij te houden.

Afspraken (voorstel):

- werken op **feature branches** voor nieuwe functionaliteit,
- duidelijke, betekenisvolle commit messages,
- regelmatig mergerequests of pull requests (indien GitHub/GitLab),
- main/master blijft in principe **compileerbaar** en **testbaar**.

Enkele handige Git-commando’s:

```bash
# Nieuwe branch maken
git checkout -b feature/nieuwe-functionaliteit

# Wijzigingen toevoegen en committen
git add .
git commit -m "Beschrijf kort maar duidelijk wat er is veranderd"

# Branch pushen
git push origin feature/nieuwe-functionaliteit
```

Meer info: <https://git-scm.com/doc>

---

## 9. AI-gebruik

Volgens de richtlijnen van het vak mogen we **generatieve AI** gebruiken, met beperkingen:

- AI gebruiken we enkel om:
  - syntax en tooling beter te begrijpen,
  - design patterns en architectuurbeslissingen te verkennen,
  - foutmeldingen of bugs te analyseren.
- Alle **definitieve code en UML** moeten we zelf begrijpen en kunnen uitleggen.
- We houden een **kort logboek** bij met:
  - wanneer AI gebruikt werd,
  - voor welk probleem,
  - en hoe we de output hebben aangepast of verbeterd.

---

## 10. TODO-lijst

Hieronder houden we bij wat er nog moet gebeuren.  
Dit is een **interne planning** voor ons team.

### Algemeen

- [ ] README aanvullen met echte namen, r-nummers en repo-URL
- [ ] Basisproject opzetten (package-structuur, main-class, build-tool configureren)
- [ ] Mapstructuur afstemmen op labs (src/main/java, src/test/java, …)

### Domein & logica

- [ ] Domeinmodel uitwerken (`Recipe`, `Ingredient`, `MealPlan`, `MealSlot`, `GroceryList`, …)
- [ ] Eerste versie `RecipeRepository` + persistentiestrategie kiezen
- [ ] Logica voor genereren van boodschappenlijst implementeren
- [ ] Observer-koppeling tussen planning en boodschappenlijst uitwerken

### Design patterns

- [ ] Singleton-klasse kiezen en implementeren (bv. config of datastore)
- [ ] Observer-interface en concrete observers implementeren
- [ ] Creational pattern (Factory/Builder/Abstract Factory) ontwerpen en toevoegen
- [ ] Extra patroon (bv. Strategy of Command) kiezen en integreren

### GUI

- [ ] Basis-GUI opzetten (receptenoverzicht, weekplanning, boodschappenlijst)
- [ ] MVC-structuur doorvoeren in code (Model, View, Controller)
- [ ] Use case “recept toevoegen” volledig werkend maken
- [ ] Use case “weekplan opstellen + boodschappenlijst genereren” volledig werkend maken

### UML & documentatie

- [ ] Globaal klassendiagram opstellen
- [ ] Mini-klassendiagrammen per pattern maken
- [ ] Use case diagram tekenen
- [ ] Minstens één sequentiediagram uitwerken
- [ ] UML updaten bij belangrijke ontwerpwijzigingen

### Tests

- [ ] Unit tests schrijven voor belangrijkste domeinklassen
- [ ] Integratietest opzetten voor realistische use case
- [ ] Testcoverage nakijken en waar nodig uitbreiden

---

## 11. Credits

Indien we code of ideeën hergebruiken van externe bronnen, vermelden we die hier.

Voorbeelden:

- StackOverflow antwoorden over specifieke Java-problemen  
- Officiële documentatie (Java, JUnit, Maven, …)  
- Tutorials of YouTube-video’s over design patterns of Java GUI

Formaat (voorbeeld):

- “Opzet Builder pattern geïnspireerd door voorbeeld uit \<link>”  
- “Git branching strategie gebaseerd op blogpost \<link>”

---

_Einde van README – versie 1 (NL, project-specifiek)._  
