# Shopping Cart Architecture Documentation (Phase 1)

## System Overview
The backend is structured as a **Modular Monolith** using Maven multi-module aggregation. Cross-cutting security concerns are compiled into an independent library module (`shopping-cart-security`), while domain entities and transactional workflows reside in the API application module (`shopping-cart-api`).

---

## Bounded Contexts & Data Ownership

| Aggregate | Data Store | Key Responsibilities & Invariants |
| :--- | :--- | :--- |
| **AppUser** | PostgreSQL | Unique username/email. BCrypt password hashing. Role-based privileges (`CUSTOMER`, `SELLER`, `ADMIN`). |
| **RefreshToken** | PostgreSQL | SHA-256 hashed token ID, family UUID, revocation timestamps. Token reuse revokes the entire family. |
| **Product** | PostgreSQL | SKU uniqueness, positive pricing (`BigDecimal`), non-negative stock, optimistic JPA versioning. |
| **Thumbnail** | MongoDB GridFS | Binary image chunks. Stored independently of relational schema. |
| **CartItem** | PostgreSQL | Unique per `(customer_id, product_id)`. Quantity validation against live inventory. |
| **PurchaseOrder** | PostgreSQL | Immutable order records. Snapshot of product names and unit prices at time of transaction. |

---

## Concurrency & Transaction Management

### 1. Dual-Datastore Compensation Pattern
Because PostgreSQL and MongoDB GridFS cannot share a two-phase commit:
1. Validate image format and file size.
2. Store binary image chunks in MongoDB GridFS $\rightarrow$ obtain `ObjectId`.
3. Save structured product entity to PostgreSQL.
4. **Compensation Trigger**: If PostgreSQL commit fails (e.g. unique constraint violation), automatically delete the uploaded MongoDB file.

### 2. Pessimistic Lock Checkout
To prevent overselling under high concurrency:
1. `CartServiceImpl.checkout()` executes `ProductRepository.findAllByIdInForUpdate(productIds)`.
2. PostgreSQL acquires a row-level write lock (`FOR UPDATE`) on all items in the cart.
3. Quantities are revalidated against locked stock.
4. Stock is decremented, `PurchaseOrder` is written, and cart lines are purged in one atomic transaction.

---

## Security Design

Authentication is stateless for access requests and stateful for refresh-token revocation:

```text
register/login -> authenticate password -> short-lived access JWT
                                  \-> long-lived refresh JWT in HttpOnly cookie

API request -> Bearer access JWT -> JWT filter -> SecurityContext -> RBAC rule
refresh request -> HttpOnly cookie -> validate hashed token record -> rotate token
logout -> revoke token record -> clear refresh cookie