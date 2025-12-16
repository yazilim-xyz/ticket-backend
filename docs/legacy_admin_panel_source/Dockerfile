# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# POM ve kaynakları kopyala
COPY pom.xml ./
COPY src ./src

# Bağımlılıkları indir ve jar oluştur
RUN mvn clean package -DskipTests

# ---- Run stage ----
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Build edilmiş jar'ı kopyala
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
