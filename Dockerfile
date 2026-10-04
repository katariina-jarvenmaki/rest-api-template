# Build stage: the image Gradle matches the wrapper, so no wrapper download and the same toolchain as ./gradlew.
FROM gradle:9.7.1-jdk25 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
USER gradle
RUN gradle --no-daemon test bootJar

# Runtime stage: JRE only, no JDK, no Gradle, no build sources.
FROM eclipse-temurin:25-jre
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
# Jar name tracks version in build.gradle; exact name so a -plain jar is never picked up
COPY --from=build /home/gradle/src/build/libs/rest-api-template-0.1.0.jar app.jar
# Non-root: uid 1000, no shell entry needed
USER 1000
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]