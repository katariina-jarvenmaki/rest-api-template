# Release v0.1.0 - First template release

**Release Date:** October 4, 2026

## About this Release

A template for app-shaped projects, not a runnable product. The idea of the repository: Copy it, keep the layered structure and the infrastructure plumbing, replace the Item domain with your own. It is not meant to be hosted publicly as an app yet: there is no authentication, and the API, the web UI and the H2 console are open to anyone who can reach the app. Native boot binds `127.0.0.1` and Docker publishes on `127.0.0.1` only, which limits exposure to local processes but never replaces authentication.

## Usage

Fresh install and endpoint listings live in the README, native mode under "Local development", Docker mode under "Run in Docker".

```bash
# Native & H2
./gradlew bootRun

# Docker & PostgreSQL:
cp .env.example .env   # then generate a password: openssl rand -base64 32
docker compose up -d --build
```

## Known friction

**A volume from before Flyway crashes startup on purpose.** A leftover `postgres_data` volume keeps its pre-Flyway table state and its old database credentials, so on a fresh start the database container comes up healthy while the app crash-loops with `password authentication failed` from Flyway.

```bash
docker compose down -v && docker compose up -d
```

**The `.env` password placeholder is a placeholder.** `POSTGRES_PASSWORD=***` in `.env.example` is there to be replaced, generate one with `openssl rand -base64 32`.

## Not included (on purpose, for now)

- **No authentication.** Spring Security is not wired yet. This is the main difference between "template" and "runnable app".
- **No CORS configuration.** The UI and the API share one origin on port 8090, so the API is same-origin by design.
- **CI on the GitHub mirror is inert.** The workflow runs on Gitea Actions, the mirrored copy of the file does nothing there.

## Components

| Component | Version | Source of the number |
|---|---|---|
| Spring Boot | 4.1.1 | build.gradle plugin |
| Java | 25 | build.gradle toolchain |
| Gradle | 9.7.1 | Dockerfile `gradle:9.7.1-jdk25` |
| Spring Data JPA + Hibernate | Boot 4.1.1 managed | build.gradle starter |
| Flyway | 12.4.0 | Container classpath `flyway-core-12.4.0.jar` |
| springdoc OpenAPI + Swagger UI | 3.1.1 | build.gradle |
| H2 | Boot 4.1.1 managed | build.gradle |
| PostgreSQL | 18-alpine | compose.yaml, `docker inspect` |
| PostgreSQL JDBC driver | 42.7.13 | Container classpath `postgresql-42.7.13.jar` |
| License | MIT | LICENSE |

## Version history

| Version | Date | Note |
|---|---|---|
| v0.1.0 | 2026-10-04 | First template release |

Git tags are the durable per-release anchors

## License

MIT. Copyright (c) 2026 Katariina Järvenmäki.