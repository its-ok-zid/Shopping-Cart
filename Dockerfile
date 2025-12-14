# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# First copy only pom to cache dependencies
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline

# Now copy the source code
COPY src ./src

# Build jar (skip tests for speed)
RUN mvn -q -e -B clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 9081

# JVM options (optional, tweak later)
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
