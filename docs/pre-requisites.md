## Prerequisites

- Java 20 installed
- Maven installed and available in `PATH`
- PostgreSQL running locally on port `5432`

Create these databases before running the project:

- `event_insider`
- `event_insider_test`

Recommended local database credentials:

- username: `admin`
- password: `admin`

## Database Setup

Run Commands

```sql
CREATE DATABASE event_insider;
CREATE DATABASE event_insider_test;
```
The project uses Flyway migrations, so tables and sample records are created automatically on startup.

## Running The Application

From the repository root:

```bash
mvn clean install
mvn -pl event-insider-application spring-boot:run -Dspring-boot.run.profiles=stage
```

Why `stage` profile for local review:

- it provides a PostgreSQL datasource configuration
- it enables Swagger UI

Local application defaults used by the `stage` profile:

- database: `jdbc:postgresql://localhost:5432/event_insider`
- username: `admin`
- password: `admin`

Once the app starts, useful URLs are:

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI docs: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Actuator health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## Authentication Headers

Most API endpoints can be protected by an SSO header.

For local `stage` profile runs, SSO validation is disabled by default, so the API can be tested directly.

If you want to enable SSO validation manually, set:

```bash
APP_REQUIRE_SSO_TOKEN=true
APP_SSO_TOKEN=dev-sso-token
```

Then send this header on protected endpoints:

```text
X-SSO-Token: dev-sso-token
```

The payment webhook endpoint does not use the SSO header. It uses:

```text
X-Payment-Webhook-Secret: dev-webhook-secret
```

## Running Tests

From the repository root:

```bash
mvn test
```

The integration tests expect a PostgreSQL database named `event_insider_test` on localhost with:

- username: `admin`
- password: `admin`

Notable tests:

- `EventApiIntegrationTest`: end-to-end API validation
- `BassiMumbaiConcurrentBookingIntegrationTest`: verifies concurrent booking behavior for limited seats

## Seed Data Available After Startup

Flyway inserts sample records automatically. Some useful IDs for review:

- user `1`: `Deepak`
- event `1`: `Arijit Singh Live Concert`
- event `2`: `Bas kar Bassi by Bassi`
- show `1`: Arijit concert show
- show `3`: Bassi Mumbai show
- event seat category `1`: Arijit `BRONZE`
- event seat category `7`: Bassi `BRONZE`
- seat inventories `1..9`: seeded seats for show `1`
## Prerequisites

- Java 20 installed
- Maven installed and available in `PATH`
- PostgreSQL running locally on port `5432`

Create these databases before running the project:

- `event_insider`
- `event_insider_test`

Recommended local database credentials:

- username: `admin`
- password: `admin`

## Database Setup

Run Commands

```sql
CREATE DATABASE event_insider;
CREATE DATABASE event_insider_test;
```
The project uses Flyway migrations, so tables and sample records are created automatically on startup.

## Running The Application

From the repository root:

```bash
mvn clean install
mvn -pl event-insider-application spring-boot:run -Dspring-boot.run.profiles=stage
```

Why `stage` profile for local review:

- it provides a PostgreSQL datasource configuration
- it enables Swagger UI

Local application defaults used by the `stage` profile:

- database: `jdbc:postgresql://localhost:5432/event_insider`
- username: `admin`
- password: `admin`

Once the app starts, useful URLs are:

- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- OpenAPI docs: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- Actuator health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

## Authentication Headers

Most API endpoints can be protected by an SSO header.

For local `stage` profile runs, SSO validation is disabled by default, so the API can be tested directly.

If you want to enable SSO validation manually, set:

```bash
APP_REQUIRE_SSO_TOKEN=true
APP_SSO_TOKEN=dev-sso-token
```

Then send this header on protected endpoints:

```text
X-SSO-Token: dev-sso-token
```

The payment webhook endpoint does not use the SSO header. It uses:

```text
X-Payment-Webhook-Secret: dev-webhook-secret
```

## Running Tests

From the repository root:

```bash
mvn test
```

The integration tests expect a PostgreSQL database named `event_insider_test` on localhost with:

- username: `admin`
- password: `admin`

Notable tests:

- `EventApiIntegrationTest`: end-to-end API validation
- `BassiMumbaiConcurrentBookingIntegrationTest`: verifies concurrent booking behavior for limited seats

## Seed Data Available After Startup

Flyway inserts sample records automatically. Some useful IDs for review:

- user `1`: `Deepak`
- event `1`: `Arijit Singh Live Concert`
- event `2`: `Bas kar Bassi by Bassi`
- show `1`: Arijit concert show
- show `3`: Bassi Mumbai show
- event seat category `1`: Arijit `BRONZE`
- event seat category `7`: Bassi `BRONZE`
- seat inventories `1..9`: seeded seats for show `1`