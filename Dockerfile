# Étape de construction
#FROM maven:3.8.5-openjdk-17 AS build
#WORKDIR /app
#COPY . .
#RUN mvn clean package -DskipTests

# Étape finale (exécution)
#FROM eclipse-temurin:17-jre-alpine
#WORKDIR /app
# Copie du jar en utilisant le wildcard (*) pour plus de flexibilité
#COPY --from=build /app/target/*.jar app.jar

#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "app.jar"]

# ==========================================
# 1. Étape de construction (Build)
# ==========================================
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ==========================================
# 2. Étape finale (Exécution)
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Sécurité : On s'assure que les certificats HTTPS d'Alpine sont à jour
RUN apk update && apk add --no-cache ca-certificates && update-ca-certificates

# Copie du JAR généré
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 🚀 Correction : On force Java à préférer l'IPv4 pour éviter le blocage de Google
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
