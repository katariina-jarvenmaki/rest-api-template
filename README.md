# Java REST API Template

Generic Java REST API template built with Spring Boot 4 and Java 25. Spring Web, Spring Data JPA and an embedded H2 database are wired up, so it boots out of the box. The template comes with five example endpoints: GET /items, GET /items/{id}, POST /items, PUT /items/{id} and DELETE /items/{id}. A small browser UI for the same items is included.

The template has no authentication: the API, the web UI and the h2-console are all open to anyone who can reach the app. Add Spring Security before using it for anything real.

## Installation

### Local development

**Requires JDK 25. On Ubuntu/Debian:**
```bash
sudo apt install openjdk-25-jdk-headless
```

**Run or restart the application (first run downloads Gradle, may take a few minutes):**
```bash
./gradlew bootRun
```

**Stop the application:**
```text
Ctrl+C
```

**Stop the Gradle daemon JVMs:**
```bash
./gradlew --stop
```

**Run the compiler (no app start):**
```bash
./gradlew compileJava
```

**Run the tests:**
```bash
./gradlew test
```

**Try the API (while the app runs):**
```bash
# List items: 200, the list starts empty
curl -i http://localhost:8090/items

# Create: 201, the database assigns the id (a body-sent id is ignored)
curl -i -X POST http://localhost:8090/items -H "Content-Type: application/json" \
    -d '{"name":"First item","description":"Optional description"}'

# Read one: 200 with the item, 404 when the id does not exist
curl -i http://localhost:8090/items/1

# Update: 200 with the new values
curl -i -X PUT http://localhost:8090/items/1 -H "Content-Type: application/json" \
    -d '{"name":"Renamed","description":"Updated description"}'

# Delete: 204, no body
curl -i -X DELETE http://localhost:8090/items/1
```

### Open the web UI (while the app runs):
```text
http://localhost:8090/
```
The UI covers the same five operations in the browser: listing, viewing, adding, editing and deleting items. Deleting asks for confirmation first. The page is served by the app from src/main/resources/static and styled with the vendored Simple.css.

## Spring Initializr settings

- Project: Gradle - Groovy
- Language: Java
- Spring Boot: 4.1.1

### Project Metadata:

- Group: com.restapitemplate
- Artifact: rest-api-template
- Package name: com.restapitemplate.restapitemplate
- Packaging: Jar
- Configuration: Properties
- Java: 25
- Dependencies: Spring Data JPA, Spring Web, Lombok, Spring Boot DevTools, H2 Database

## Project notes

Using server port 8090 for this project everywhere as the host 8080 is already occupied by an unrelated process on the dev machine.

Development now shows Hibernate's SQL in the bootRun console (spring.jpa.show-sql=true).

### Browse the in-memory database (while the app runs):
```text
http://localhost:8090/h2-console
```
The database resets every time the app stops.
