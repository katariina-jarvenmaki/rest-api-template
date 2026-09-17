# Java REST API Template

Generic Java REST API template built with Spring Boot 4 and Java 25. Spring Web, Spring Data JPA and an embedded H2 database are wired up, so it boots out of the box.

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

**Run the compiler**
```bash
./gradlew compileJava
```

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