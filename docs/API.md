# Shopping Cart API Contract (Phase 1)

Base path: `/api`. All responses use JSON wrapped in `ApiResponse<T>`. Errors return `{ "success": false, "code": "...", "message": "...", "traceId": "..." }`.

---

## 1. Authentication Endpoints

| Method | Route | Auth | Purpose |
| --- | --- | --- | --- |
| `POST` | `/auth/register` | Public | Register customer account. Automatically assigns `CUSTOMER` role. |
| `POST` | `/auth/login` | Public | Verify password, return access token in body, set `shopping_refresh` cookie. |
| `POST` | `/auth/refresh` | Cookie | Validate hashed token, rotate refresh token, return new access token. |
| `POST` | `/auth/logout` | Authenticated | Revoke refresh token family and clear `shopping_refresh` cookie. |

---

## 2. User & Admin Endpoints

| Method | Route | Auth | Purpose |
| --- | --- | --- | --- |
| `GET` | `/users/me` | Authenticated | Read currently authenticated user's profile. |
| `PATCH` | `/users/me` | Authenticated | Update display name. |
| `PUT` | `/admin/users/{id}/roles` | `ADMIN` | Update roles for a specific user (`CUSTOMER`, `SELLER`, `ADMIN`). |

---

## 3. Product Catalog Endpoints

| Method | Route | Auth | Purpose |
| --- | --- | --- | --- |
| `GET` | `/products` | Public | Page active products (`page`, `size`). |
| `GET` | `/products/{id}` | Public | Read single active product. |
| `GET` | `/products/{id}/thumbnail` | Public | Stream binary thumbnail image from MongoDB GridFS. |
| `POST` | `/seller/products` | `SELLER` | Create product with optional multipart image upload. |
| `PATCH` | `/seller/products/{id}` | Owning `SELLER` | Update product SKU, name, description, price, or stock. |
| `PATCH` | `/seller/products/{id}/stock` | Owning `SELLER` | Update stock level with inventory reason. |
| `DELETE` | `/seller/products/{id}` | Owning `SELLER` | Deactivate product (soft-delete). |

---

## 4. Cart & Order Endpoints

| Method | Route | Auth | Purpose |
| --- | --- | --- | --- |
| `GET` | `/cart` | `CUSTOMER` | Read current cart and calculated total. |
| `POST` | `/cart/items` | `CUSTOMER` | Add item to cart with stock validation. |
| `PATCH` | `/cart/items/{cartItemId}` | Owning `CUSTOMER` | Update cart item quantity with stock validation. |
| `DELETE` | `/cart/items/{cartItemId}` | Owning `CUSTOMER` | Remove item line from cart. |
| `POST` | `/cart/checkout` | `CUSTOMER` | Concurrency-safe atomic checkout (pessimistic lock, stock decrement, receipt). |
| `GET` | `/orders` | `CUSTOMER` | Page current customer's order history. |
| `GET` | `/orders/{id}` | Owning `CUSTOMER` / `ADMIN` | Read single order receipt. |