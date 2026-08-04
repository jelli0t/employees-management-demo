# ---- Build stage ----
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy Maven Wrapper and POM first to leverage Docker layer caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw

# Download dependencies (cached unless pom.xml / wrapper changes)
RUN ./mvnw dependency:go-offline -B

# Copy sources and package
COPY src src
RUN ./mvnw package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/employees-management-demo-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
