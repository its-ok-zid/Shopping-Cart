# Shopping Cart API Architecture

## Purpose and scope

This repository contains the backend API for a small marketplace. It does not
render a storefront UI. Clients use the HTTP API to browse products, manage a
cart, check out, and—when authorised—manage a seller's catalogue.

The system deliberately models the common marketplace roles:

| Role | Capabilities |
| --- | --- |
| `CUSTOMER` | Browse and search active products; manage only their own cart; check out; read only their own orders. |
| `SELLER` | All customer capabilities plus create, edit, deactivate, and restock only products they own. |
| `ADMIN` | Manage user roles and perform cross-seller catalogue administration. |

There is no payment-provider integration in this project. Checkout validates
stock, creates an order, decrements stock, clears the cart, and returns an
order confirmation. A real payment provider belongs behind a separate payment
port and must complete before an order is marked paid.

## Current API inventory

The original API exposes the following routes. They are retained here as an
audit record; the implementation is replaced by the versioned contract in
`docs/API.md`.

| Method | Route | Current behaviour | Problem addressed by the refactor |
| --- | --- | --- | --- |
| `GET` | `/api/items` | Lists all items. | No paging, search, stock filter, or consistent resource name. |
| `GET` | `/api/items/{id}` | Reads an item directly from the repository. | Returns the persistence entity and has a different response shape. |
| `POST` | `/api/items` | Creates an item and uploads a GridFS image. | Public write access and no seller owner. |
| `PUT` | `/api/items/{id}` | Fully replaces an item and may replace its image. | Public write access; not a partial update; cleanup can race a database commit. |
| `POST` | `/api/usercart/add` | Adds a named item to a cart. | Trusts arbitrary user IDs, uses product names, and does not check stock. |
| `GET` | `/api/usercart/all` | Lists every cart row. | Exposes all customers' carts. |
| `GET` | `/api/usercart/user` | Lists a user cart by request parameter. | Trusts arbitrary user IDs. |
| `PUT` | `/api/usercart/modify` | Changes quantity or deletes at zero. | Does not enforce stock and has an inconsistent response. |
| `DELETE` | `/api/usercart/remove` | Deletes a cart row. | Trusts arbitrary user IDs. |

There is currently no user-registration endpoint, product-image read endpoint,
seller catalogue endpoint, product deactivation endpoint, checkout endpoint,
order model, authentication, or authorisation.

## Module boundaries

The Maven reactor is split so security can be compiled and tested as a
standalone library and then consumed by the API.

```text
shopping-cart-platform                     (parent / aggregator POM)
├── shopping-cart-security                 (JAR; framework/security concern)
│   ├── JWT creation and validation
│   ├── JWT request filter and 401/403 responses
│   ├── refresh-token rotation contracts
│   ├── password encoder and authentication contracts
│   └── no JPA entities or application tables
└── shopping-cart-api                      (Spring Boot application)
    ├── domain: users, products, cart, orders, refresh-token persistence
    ├── application services and transaction boundaries
    ├── REST controllers and DTO mapping
    ├── security-library adapters for user and refresh-token persistence
    ├── PostgreSQL migrations
    └── MongoDB GridFS thumbnail adapter
```

The dependency direction is one way: the API depends on the security library.
The security module knows only small ports (for loading credentials and saving
hashed refresh-token identifiers). It never imports API JPA entities. This
keeps the library reusable and prevents a circular dependency.

## Layered design

```text
HTTP Controller
    -> request DTO validation
Application service (use case and transaction boundary)
    -> domain entity / policy
Repository or external-storage port
    -> PostgreSQL / MongoDB GridFS
```

- Controllers never access repositories and never return JPA entities.
- DTOs are immutable records where possible and carry boundary validation.
- Services own use cases, authorisation checks, state transitions, and
  transactional consistency.
- Repositories are infrastructure concerns only.
- Global error handling maps domain errors to stable HTTP status codes without
  returning database exception text.

## Data ownership and invariants

| Aggregate | Owner | Key invariants |
| --- | --- | --- |
| `AppUser` | Authentication / admin | Email and username are unique. Passwords are BCrypt hashes only. Registration assigns only `CUSTOMER`. |
| `Product` | Seller | A product has exactly one seller, a non-negative stock count, a positive monetary price, and an active flag. |
| `CartItem` | Customer | References one product; one row per `(customer, product)`; quantity is positive. |
| `Order` | Customer | Created only from a non-empty cart after stock is revalidated. Order lines preserve product name and unit-price snapshots. |
| `RefreshToken` | Authentication | Stores a one-way hash of a token identifier, expiry, revocation state, and rotation relationship—never a raw token. |

`Product` uses a JPA version column and checkout takes a pessimistic write lock
for products being bought. This prevents two concurrent checkouts from both
selling the final unit.

## Image consistency

Product data is in PostgreSQL; image bytes are in MongoDB GridFS. They cannot
share one database transaction. The API uses compensation instead:

1. Validate the image type and size before storing it.
2. Store a new GridFS object.
3. Save and flush the product change in PostgreSQL.
4. On SQL failure, delete the newly uploaded GridFS object.
5. After a successful SQL commit, delete the replaced GridFS object.

The result is safe against the common failure modes without pretending that the
two databases form a distributed transaction.

## Security design

Authentication is stateless for access requests and stateful for refresh-token
revocation:

```text
register/login -> authenticate password -> short-lived access JWT
                                  \-> long-lived refresh JWT in HttpOnly cookie

API request -> Bearer access JWT -> JWT filter -> SecurityContext -> RBAC rule
refresh request -> HttpOnly cookie -> validate hashed token record -> rotate token
logout -> revoke token record -> clear refresh cookie
```

- Passwords use `BCryptPasswordEncoder`; plaintext is never persisted or logged.
- Access tokens are short-lived and returned in the login/refresh response for
  the client to hold in memory and send as `Authorization: Bearer <token>`.
- Refresh tokens are sent only in a `Secure`, `HttpOnly`, `SameSite` cookie.
  Their token identifier is hashed before being stored in PostgreSQL.
- Refresh tokens rotate on every refresh. Reuse of a revoked token revokes the
  active token family.
- CSRF protection remains enabled for cookie-authenticated endpoints. The API
  does not authenticate normal requests from the cookie, which limits CSRF
  exposure for the access-token API.
- Development profiles may relax only the cookie `Secure` flag for localhost;
  production requires HTTPS and allowed-origin configuration.

## Main request flows

### Customer purchase

```text
Browse/search -> Product query (active catalogue)
Add/update cart -> validate product + requested quantity <= stock
Checkout -> lock products -> revalidate -> create order -> decrement stock -> clear cart
```

### Seller inventory

```text
Seller access JWT -> RBAC and ownership check -> update product / upload image / restock
```

An ownership check always compares the authenticated principal with the
product's seller ID; a seller-supplied request parameter is never trusted.

## Configuration and deployment

- Credentials come from environment variables or a secret manager, never a
  committed properties file.
- Flyway owns PostgreSQL schema changes. Hibernate validates the schema but
  never changes it in production.
- Docker Compose uses matching Spring environment variables and health checks.
- Tests use isolated PostgreSQL and MongoDB containers or a dedicated test
  profile; they never contact the configured cloud databases.

## Legacy data migration

The existing `item`, `user_cart`, and `app_user_detail` schema is treated as
legacy input. Flyway will add the new schema in forward-only migrations and
backfill product and cart references where possible. Existing rows are not
dropped. Legacy products without an assigned seller are placed under a
controlled migration seller so that ownership is explicit before seller RBAC
is enabled.
