# Étape de construction
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Étape finale (exécution)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copie du jar en utilisant le wildcard (*) pour plus de flexibilité
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
