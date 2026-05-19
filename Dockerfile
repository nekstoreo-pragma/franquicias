# ---- Build stage ----
FROM eclipse-temurin:25-jdk-noble AS builder
WORKDIR /app

# Build files copied before source to let Docker cache the dependency layer
COPY gradlew settings.gradle build.gradle main.gradle gradle.properties ./
COPY gradle/ gradle/
COPY domain/model/build.gradle                                    domain/model/
COPY domain/usecase/build.gradle                                   domain/usecase/
COPY infrastructure/entry-points/reactive-web/build.gradle         infrastructure/entry-points/reactive-web/
COPY infrastructure/driven-adapters/dynamo-db/build.gradle         infrastructure/driven-adapters/dynamo-db/
COPY infrastructure/helpers/metrics/build.gradle                   infrastructure/helpers/metrics/
COPY applications/app-service/build.gradle                         applications/app-service/
RUN ./gradlew dependencies --no-daemon --no-configuration-cache

COPY . .
RUN ./gradlew :app-service:bootJar --no-daemon --no-configuration-cache -x test -x validateStructure

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre-noble
WORKDIR /app

COPY --from=builder /app/applications/app-service/build/libs/Franchise.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
