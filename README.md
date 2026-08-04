# Employees Management

Simple Spring Boot CRUD service for managing employees. Uses an in-memory H2 database, JPA, MapStruct, and Springdoc OpenAPI.

## Requirements

- **Java 17+** (JDK) — if running with Maven Wrapper
- **Docker** — if running with Docker (no local JDK required)
- No local Maven install needed — the project includes the [Maven Wrapper](https://maven.apache.org/wrapper/)

## Getting started with the Maven Wrapper

From the project root:

### macOS / Linux

```bash
# Make the wrapper executable (first time only)
chmod +x mvnw

# Run tests (optionally)
./mvnw test

# Run the application
./mvnw spring-boot:run
```

### Windows

```cmd
mvnw.cmd spring-boot:run
```

The app starts on **http://localhost:8080**.

On first run, the wrapper downloads the Maven distribution defined in `.mvn/wrapper/maven-wrapper.properties` (currently Maven **3.9.16**).

### Other useful commands

```bash
# Compile
./mvnw compile

# Run tests
./mvnw test

# Package (creates a jar under target/)
./mvnw package

# Run the packaged jar
java -jar target/employees-management-demo-0.0.1-SNAPSHOT.jar
```

On Windows, replace `./mvnw` with `mvnw.cmd`.

## Getting started with Docker

As an alternative, you can build and run the app with Docker (no local JDK or Maven required):

```bash
docker build -t employees-management .
docker run -p 8080:8080 employees-management
```

The app starts on **http://localhost:8080**.

## Useful URLs

| Resource   | URL |
|------------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI    | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

**H2 console connection settings:**

- JDBC URL: `jdbc:h2:mem:employees-management-db`
- Username: `admin`
- Password: `admin`

## API overview

Base path: `/api/v1/employees`

| Method   | Path              | Description                          |
|----------|-------------------|--------------------------------------|
| `POST`   | `/`               | Create employee                      |
| `GET`    | `/`               | List / search (`name`, `team`, `teamLead`) |
| `GET`    | `/{id}`           | Get employee by ID                   |
| `PATCH`  | `/{id}`           | Update employee                      |
| `DELETE` | `/{id}`           | Delete employee                      |

## Tech stack

- Spring Boot 3.5
- Spring Data JPA
- H2 (in-memory)
- MapStruct
- Lombok
- Springdoc OpenAPI
