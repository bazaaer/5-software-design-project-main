This is the short version of how to run the app in the different modes I actually use.

H2 (default). This is the easy one. DB_TYPE is already set to h2 in `project/.env`, so just run it from the repo root:

```
cd project
mvn -f pom.xml clean org.openjfx:javafx-maven-plugin:0.0.8:run
```

That uses the file based H2 database at `./data/mealplanner` and it starts fresh if you delete the files in that folder.

Postgres. Set `DB_TYPE=postgres` in `project/.env`. The docker-compose reads the same environment variables. Then run:

```
cd project
docker compose up -d
mvn -f pom.xml clean org.openjfx:javafx-maven-plugin:0.0.8:run
```

If Postgres is running somewhere else, just point `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` to that server instead of using docker.

Console demo. There is also a `Main` class that prints a demo run in the console. 
