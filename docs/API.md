# Shopping Cart API Contract

Base path: `/api`. All responses use JSON. Successful responses use
`ApiResponse<T>`; errors use `{ "success": false, "code": "...", "message": "...",
"traceId": "..." }`.

Authentication endpoints may set or clear the refresh-token cookie. All other
authenticated endpoints require `Authorization: Bearer <access-token>`.

## Authentication and account endpoints

| Method | Route | Authentication | Purpose |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Public | Register a customer account. The caller cannot assign roles. |
| `POST` | `/auth/login` | Public | Verify password, return an access token, set refresh-token cookie. |
| `POST` | `/auth/refresh` | Refresh cookie + CSRF protection | Rotate the refresh token and return a new access token. |
| `POST` | `/auth/logout` | Access token or refresh cookie | Revoke refresh session and clear its cookie. |
| `GET` | `/users/me` | Any authenticated user | Read the current account. |
| `PATCH` | `/users/me` | Any authenticated user | Update permitted account profile fields. |
| `PUT` | `/admin/users/{id}/roles` | `ADMIN` | Grant/revoke seller or admin roles. |

## Product catalogue endpoints

| Method | Route | Authentication | Purpose |
| --- | --- | --- | --- |
| `GET` | `/products` | Public | Page/search active products. Query: `q`, `sellerId`, `inStock`, `page`, `size`, `sort`. |
| `GET` | `/products/{id}` | Public | Read an active product. |
| `GET` | `/products/{id}/thumbnail` | Public | Stream the validated product thumbnail. |
| `POST` | `/seller/products` | `SELLER` | Create a product owned by the current seller. Supports multipart product JSON plus optional image. |
| `GET` | `/seller/products` | `SELLER` | Page only the current seller's products, including inactive products. |
| `PATCH` | `/seller/products/{id}` | Owning `SELLER` | Partially update product text, price, stock, or image. |
| `PATCH` | `/seller/products/{id}/stock` | Owning `SELLER` | Restock or set stock with an inventory reason. |
| `DELETE` | `/seller/products/{id}` | Owning `SELLER` | Deactivate, rather than erase, a product. |
| `PATCH` | `/admin/products/{id}` | `ADMIN` | Cross-seller product moderation where needed. |

For backwards compatibility, the old `/items` and `/usercart` routes are not
left enabled: unauthenticated, ID-in-request routes are a security flaw. API
clients must use this contract.

## Cart and order endpoints

| Method | Route | Authentication | Purpose |
| --- | --- | --- | --- |
| `GET` | `/cart` | `CUSTOMER` | Read the current customer's cart and computed total. |
| `POST` | `/cart/items` | `CUSTOMER` | Add `{ productId, quantity }`; combines the line if it already exists. |
| `PATCH` | `/cart/items/{cartItemId}` | Owning `CUSTOMER` | Set a positive quantity after stock validation. |
| `DELETE` | `/cart/items/{cartItemId}` | Owning `CUSTOMER` | Remove a cart line. |
| `POST` | `/cart/checkout` | `CUSTOMER` | Create an order, decrement stock atomically, clear the cart, return confirmation. |
| `GET` | `/orders` | `CUSTOMER` | Page the current customer's order history. |
| `GET` | `/orders/{id}` | Owning `CUSTOMER` or `ADMIN` | Read one order. |

## Important response semantics

- A product whose stock is zero remains viewable (unless inactive) and returns
  `inStock: false`; cart and checkout return `409 INSUFFICIENT_STOCK`.
- Checkout does not reserve stock while an item merely sits in a cart.
- Product lookup and cart operations use immutable product IDs, never names.
- Monetary fields use decimal JSON values and are represented by `BigDecimal`
  in Java/PostgreSQL.
- A `DELETE` product request only deactivates a product. Cart additions for an
  inactive product fail; existing historical order lines remain intact.
