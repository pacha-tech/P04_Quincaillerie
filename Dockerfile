
# Étape 1 : Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# 1. Copier uniquement le pom.xml d'abord
COPY pom.xml .

# 2. Télécharger les dépendances (cette étape sera mise en cache par Docker)
# Si vous ne modifiez pas le pom.xml, cette étape sera sautée au prochain build
RUN mvn dependency:go-offline -B

# 3. Copier le reste du code source
COPY src ./src

# 4. Compiler le projet
RUN mvn clean package -DskipTests -B

# Étape 2 : Exécution
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le jar généré depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Render utilise souvent la variable d'environnement PORT,
# Spring Boot la récupère automatiquement si on expose 8080
EXPOSE 8080

# Optimisation de la mémoire pour les petits plans Render (Free/Starter)
ENTRYPOINT ["java", "-Xmx512m", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]