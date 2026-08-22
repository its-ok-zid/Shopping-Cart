<div align="center">

# Shopping Cart API

### A containerized Java 21 / Spring Boot backend for catalog and cart workflows

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![MongoDB](https://img.shields.io/badge/MongoDB-GridFS-47A248?style=flat-square&logo=mongodb&logoColor=white)](https://www.mongodb.com/docs/manual/core/gridfs/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)

</div>

## Overview

Shopping Cart API is a Spring Boot backend for managing an item catalog and user cart entries. It keeps transactional application data in PostgreSQL and stores item thumbnails in MongoDB GridFS, with Docker Compose providing a reproducible local environment.

The repository's default branch, `backend`, contains the service. The project is documented from the implementation currently in that branch.

## What It Demonstrates

- **Catalog management** — create, read, list, and update items through REST endpoints.
- **Multipart uploads** — submit item metadata plus a thumbnail in one request.
- **Two datastore responsibilities** — PostgreSQL for item, user, and cart data; MongoDB GridFS for thumbnail files.
- **Consistency-minded file handling** — thumbnail uploads are compensated when item creation fails; replacement files are removed only after a successful item update.
- **Cart workflows** — add items, fetch all or per-user cart entries, modify quantities, and remove cart rows.
- **Backend structure** — controller, service, repository, DTO, model, and centralized exception-handling layers.
- **Containerized startup** — app, PostgreSQL, and MongoDB services are defined in Docker Compose.

## Architecture

### Current Implementation

The current `backend` branch is a containerized Spring Boot service with clear separation between API, business logic, relational persistence, and media storage.

```mermaid
flowchart LR
    Client["Web or API client"] --> API["Shopping Cart API<br/>Spring Boot · Java 21"]

    API --> Controllers["REST controllers"]
    Controllers --> Services["Service layer<br/>validation & business rules"]

    Services --> JPA["Spring Data JPA"]
    JPA --> Postgres[("PostgreSQL<br/>items · users · carts")]

    Services --> GridFS["GridFsTemplate"]
    GridFS --> Mongo[("MongoDB GridFS<br/>item thumbnails")]

    Compose["Docker Compose"] -. runs .-> API
    Compose -. runs .-> Postgres
    Compose -. runs .-> Mongo
```

### Target Production Architecture

The following architecture represents the **target evolution of this project** toward a production-oriented, independently scalable microservices platform. It is intentionally documented separately from the current implementation so the README does not imply that every component below is already deployed in the repository.

```mermaid
flowchart TB
    %% STYLING DEFINITIONS
    classDef client fill:#E1F5FE,stroke:#0288D1,stroke-width:2px,color:#01579B;
    classDef gateway fill:#EDE7F6,stroke:#512DA8,stroke-width:2px,color:#311B92;
    classDef service fill:#E8F5E9,stroke:#388E3C,stroke-width:2px,color:#1B5E20;
    classDef cache fill:#FFF3E0,stroke:#F57C00,stroke-width:2px,color:#E65100;
    classDef database fill:#ECEFF1,stroke:#455A64,stroke-width:2px,color:#263238;
    classDef broker fill:#FCE4EC,stroke:#C2185B,stroke-width:2px,color:#880E4F;

    %% CLIENT LAYER
    subgraph CLIENT_LAYER["1. CLIENT TIER (Omnichannel)"]
        WEB["Web Application (React / Next.js)"]:::client
        MOBILE["Mobile App (iOS / Android)"]:::client
        POSTMAN["External Partners / API Consumers"]:::client
    end

    %% EDGE & GATEWAY LAYER
    subgraph EDGE_LAYER["2. EDGE ROUTING & SECURITY GATEWAY (Port 8080)"]
        GW["API Gateway Engine"]:::gateway
        RL["Redis Token-Bucket Rate Limiter"]:::cache
        AUTH_GUARD["Edge Security & JWT Token Validator"]:::gateway
    end

    %% MICROSERVICES & RESILIENCE
    subgraph CORE_SERVICES["3. BUSINESS SERVICES DOMAIN"]
        subgraph AUTH_SERVICE_CONTAINER["Authentication Microservice (Port 8082)"]
            CB_AUTH["Resilience4j Circuit Breaker"]:::gateway
            AUTH_SVC["Auth Service Engine"]:::service
        end

        subgraph PROD_SERVICE_CONTAINER["Product Catalog Microservice (Port 8081)"]
            CB_PROD["Resilience4j Circuit Breaker"]:::gateway
            PROD_SVC["Product Service Engine"]:::service
        end

        subgraph CART_SERVICE_CONTAINER["Cart & Order Microservice (Port 8083)"]
            CB_CART["Resilience4j Circuit Breaker"]:::gateway
            CART_SVC["Cart & Order Service Engine"]:::service
        end
    end

    %% CACHE & ASYNC BROKER
    subgraph ACCELERATION_TIER["4. IN-MEMORY CACHE & EVENT BROKER"]
        REDIS_CACHE["Redis Distributed Cache (L2)"]:::cache
        EVENT_BUS["Event Message Broker (Redis Streams / Pub-Sub)"]:::broker
        WS_SERVER["WebSocket Real-Time Notification Gateway"]:::broker
    end

    %% DATA PERSISTENCE LAYER
    subgraph STORAGE_TIER["5. DATA PERSISTENCE LAYER"]
        AUTH_DB[("PostgreSQL\n(Auth & Identity DB)")]:::database
        PROD_DB[("PostgreSQL\n(Product Catalog DB)")]:::database
        GRIDFS_DB[("MongoDB Atlas\n(GridFS Media Store)")]:::database
        CART_DB[("PostgreSQL\n(Cart & Orders DB)")]:::database
    end

    %% CLIENT TO EDGE CONNECTIONS
    WEB --> GW
    MOBILE --> GW
    POSTMAN --> GW

    %% GATEWAY INTERNAL FLOW
    GW --> RL
    RL --> AUTH_GUARD

    %% GATEWAY TO SERVICE ROUTING
    AUTH_GUARD -->|/api/v1/auth/**| CB_AUTH --> AUTH_SVC
    AUTH_GUARD -->|/api/v1/products/**| CB_PROD --> PROD_SVC
    AUTH_GUARD -->|/api/v1/cart/**| CB_CART --> CART_SVC

    %% AUTH SERVICE DATA FLOW
    AUTH_SVC --> AUTH_DB

    %% PRODUCT SERVICE DATA FLOW
    PROD_SVC <-->|Cache Aside Pattern| REDIS_CACHE
    PROD_SVC -->|Structured Data| PROD_DB
    PROD_SVC -->|Binary Image Chunks| GRIDFS_DB

    %% CART SERVICE INTERACTIONS
    CART_SVC --> CART_DB
    CART_SVC -.->|Verify Pricing/Stock| CB_PROD
    CART_SVC -->|Publish Order Events| EVENT_BUS

    %% ASYNC EVENT CONSUMPTION
    EVENT_BUS -->|Trigger Stock Decrement| PROD_SVC
    EVENT_BUS -->|Push Out-of-Stock Alert| WS_SERVER
    WS_SERVER -.->|Real-Time Push| WEB
```

### Architecture Goals

| Concern | Target approach |
| --- | --- |
| Edge routing | API Gateway with centralized routing and security |
| Authentication | Dedicated authentication service with JWT validation |
| Resilience | Resilience4j circuit breakers around service dependencies |
| Rate limiting | Redis-backed token-bucket rate limiter |
| Catalog performance | Redis cache-aside strategy |
| Media storage | MongoDB Atlas GridFS |
| Service isolation | Separate PostgreSQL databases per bounded context |
| Async processing | Redis Streams / Pub-Sub for domain events |
| Real-time updates | WebSocket notification gateway |
| Scalability | Independently deployable and horizontally scalable services |

## Tech Stack

| Area | Technology |
| --- | --- |
| Language & runtime | Java 21 |
| Application framework | Spring Boot 3.2, Spring Web |
| Persistence | Spring Data JPA, PostgreSQL |
| File storage | Spring Data MongoDB, MongoDB GridFS |
| API quality | Bean Validation, Springdoc OpenAPI / Swagger |
| Build & local delivery | Maven Wrapper, Docker, Docker Compose |
| Code ergonomics | Lombok |
| Target architecture | Spring Cloud / API Gateway, Redis, Resilience4j, Redis Streams / Pub-Sub |

## Run Locally

### With Docker Compose

1. Clone the backend branch:

   ```bash
   git clone --branch backend https://github.com/its-ok-zid/Shopping-Cart.git
   cd Shopping-Cart
   ```

2. Set a database password and start the stack.

   **PowerShell**

   ```powershell
   $env:POSTGRES_PASSWORD = "change-me"
   docker compose up --build
   ```

   **macOS / Linux**

   ```bash
   POSTGRES_PASSWORD=change-me docker compose up --build
   ```

3. The API starts at `http://localhost:9081`. When the Springdoc UI is available, open `http://localhost:9081/swagger-ui/index.html`.

Docker Compose supplies the application with its PostgreSQL and MongoDB connection settings. The Postgres and Mongo volumes persist data between restarts.

### With a Local JDK

Use JDK 21 and provide the datasource and MongoDB environment variables expected by `application.properties`:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
MONGODB_URI
MONGODB_DATABASE
APP_THUMBNAIL_GRIDFS_BUCKET
```

Then run:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
./mvnw.cmd spring-boot:run
```

## API Surface

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/items` | List catalog items |
| `GET` | `/api/items/{id}` | Fetch one item |
| `POST` | `/api/items` | Create an item with a thumbnail |
| `PUT` | `/api/items/{id}` | Update item metadata and optionally replace its thumbnail |
| `POST` | `/api/usercart/add` | Add an item to a user's cart |
| `GET` | `/api/usercart/all` | List cart entries |
| `GET` | `/api/usercart/user?userId={id}` | List a user's cart entries |
| `PUT` | `/api/usercart/modify` | Change a cart item's quantity |
| `DELETE` | `/api/usercart/remove` | Remove a cart item |

### Create an Item

```bash
curl -X POST http://localhost:9081/api/items \
  -H "Accept: application/json" \
  -F 'item={"name":"Wireless Headphones","description":"Over-ear headphones","cost":99.99,"mfrNo":"WH-100","stock":20};type=application/json' \
  -F "thumbnail=@./headphones.png"
```

> Cart endpoints work with persisted users. User-provisioning endpoints are not included in this repository.

## Testing

The project includes application, controller, repository, and service-layer tests. With local dependencies configured, run:

```bash
./mvnw test
```

## Project Scope

This repository currently focuses on the backend service. The target architecture above describes the planned evolution toward independently deployable authentication, product, and cart/order services with centralized gateway security, distributed caching, resilience patterns, and asynchronous events.

## Repository Structure

```text
src/main/java/com/cts/
├── controller/        # Item and cart HTTP endpoints
├── dto/               # Request and response contracts
├── exception/         # Centralized API error handling
├── model/             # JPA entities
├── repository/        # PostgreSQL data access
├── service/           # Service contracts
└── serviceImpl/       # Catalog, cart, and GridFS implementations

src/main/resources/
└── application.properties

Dockerfile
docker-compose.yml
pom.xml
```

## Roadmap

### Current

- [x] Catalog CRUD workflows
- [x] Cart workflows
- [x] PostgreSQL persistence
- [x] MongoDB GridFS thumbnail storage
- [x] Docker Compose environment
- [x] Validation and centralized exception handling
- [x] Automated tests

### Next

- [ ] Dedicated authentication microservice
- [ ] API Gateway and centralized JWT validation
- [ ] Redis distributed caching and rate limiting
- [ ] Resilience4j circuit breakers
- [ ] Product / Cart / Order service separation
- [ ] Redis Streams / Pub-Sub domain events
- [ ] Stock reservation and asynchronous inventory updates
- [ ] WebSocket real-time notifications
- [ ] CI/CD and cloud deployment

---

Built as a portfolio project by [Zidan Ali](https://github.com/its-ok-zid).
