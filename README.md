## Software-Design-Techniques_keki
## Smart Cake Shop Management System

### Team

- **El-Ghoul Layla** — GitHub [@scarletwtc](https://github.com/scarletwtc)  
- **Mahmoud Mirghani Abdelrahman** — GitHub [@Abd210](https://github.com/Abd210)  
- **Uritu Andra-Ioana** — GitHub [@andrauritu](https://github.com/andrauritu)

---

## Microservices Implementation (Orders, Inventory, Kitchen)

This repository now contains a **microservices-based backend** for Keki’s Smart Cake Shop focused on three core services only:

- **Order Service** (`orderservice`): handles creation and status of cake orders, applies the **Factory**, **Builder**, and **Observer** patterns for `Order` and `Cake`.
- **Inventory Service** (`inventoryservice`): manages ingredient stock in its own database, inspired by the **Singleton** inventory manager but implemented as a dedicated microservice.
- **Kitchen Service** (`kitchenservice`): tracks preparation and delivery of orders in the kitchen and pushes status changes back to the Order Service.

Each service:

- Runs as an independent **Spring Boot** application.
- Owns its **own PostgreSQL database** (one DB per service).
- Exposes a **REST API** and communicates with other services over HTTP.
- Is packaged with a dedicated **Dockerfile** and wired together via `docker-compose.yml`.

The original Java desktop prototype with design patterns remains in the `SmartCakeShop` folder for reference; the microservices implementation reuses those ideas in a distributed architecture.

---

## How the Three Services Communicate

- **Order → Inventory**: when a customer places a *standard cake* order, `orderservice` calls `inventoryservice` (`/api/inventory/check-and-reserve`) to **check and reserve** flour, sugar, eggs, etc.
- **Order → Kitchen**: if inventory is sufficient, `orderservice` persists the new order (status = `IN_PROGRESS`) and calls `kitchenservice` (`/api/kitchen/orders`) to create a **kitchen order ticket**.
- **Kitchen → Order**: when the **chef** or **staff** update a kitchen order (`READY` / `DELIVERED`), `kitchenservice` calls back into `orderservice` (`/api/orders/{id}/status`) so the main order status stays in sync.

This flow implements the **“standard cake”** sequence from the diagrams in a microservices style:

1. **Customer → Order Service**: place order for a standard cake.
2. **Order Service → Inventory Service**: check stock and reserve ingredients.
3. **Order Service → Kitchen Service**: create kitchen order; status becomes `IN_PROGRESS`.
4. **Kitchen Service (Chef/Staff)**: update to `READY` then `DELIVERED`.
5. **Kitchen Service → Order Service**: pushes status updates back, so observers (Chef/Staff) in `orderservice` are notified.

Internally, `orderservice` keeps the **Observer pattern** (Chef + Staff observers attached to an `Order`) and uses a **CakeBuilder** and **OrderFactory** for creating cake orders, matching the original class diagram.

---

## Running Everything with Docker Compose

### Prerequisites

- **Docker** and **Docker Compose** installed.
- No other services already listening on ports **8081, 8082, 8083** or **5433, 5434, 5435**.

### Start the system

From the repository root:

```bash
docker compose up --build
```

This will:

- Build and start:
  - `apigateway` on `http://localhost:8080` (single entry point for clients)
  - `orderservice` on `http://localhost:8081`
  - `inventoryservice` on `http://localhost:8082`
  - `kitchenservice` on `http://localhost:8083`
- Start three PostgreSQL databases:
  - `orderservice-db` (exposed on host port `5433`)
  - `inventoryservice-db` (exposed on `5434`)
  - `kitchenservice-db` (exposed on `5435`)

To stop everything:

```bash
docker compose down
```

---

## Main REST Endpoints

### Order Service (via API Gateway, port 8080)

- **POST** `http://localhost:8080/api/orders`  
  - **Description**: Create a new *standard cake* order.
  - **Request body example**:
    ```json
    {
      "customerName": "Alice",
      "flavour": "CHOCOLATE",
      "color": "BROWN",
      "quantity": 1
    }
    ```
  - **Behaviour**:
    - Calls `inventoryservice` to check and reserve ingredients.
    - On success: saves the order, sets status `IN_PROGRESS`, notifies Chef + Staff observers, and calls `kitchenservice` to create a kitchen order.
    - On insufficient stock: returns **409 CONFLICT** with an error message.

- **GET** `http://localhost:8080/api/orders`  
  - **Description**: List all orders.

- **GET** `http://localhost:8080/api/orders/{id}`  
  - **Description**: Get a single order (including cake flavour/color, price, and status).

- **PATCH** `http://localhost:8080/api/orders/{id}/status?status=READY`  
  - **Description**: Manually update order status (used internally by `kitchenservice` for `READY` / `DELIVERED`, and can also be called from Postman for testing edge cases like `CANCELLED`).  
  - **Accepted values**: `NEW`, `IN_PROGRESS`, `READY`, `DELIVERED`, `CANCELLED`.

### Inventory Service (via API Gateway, port 8080)

- **GET** `http://localhost:8080/api/inventory`  
  - **Description**: List all ingredients and their quantities.

- **POST** `http://localhost:8080/api/inventory`  
  - **Description**: Create or update a single ingredient.  
  - **Example body**:
    ```json
    {
      "name": "Flour",
      "quantity": 100
    }
    ```

- **POST** `http://localhost:8080/api/inventory/check-and-reserve`  
  - **Description**: Atomically check and reserve stock for a set of ingredients. Used by `orderservice`.
  - **Example success request**:
    ```json
    {
      "items": {
        "Flour": 2,
        "Sugar": 1,
        "Egg": 3
      }
    }
    ```
  - **Response**:
    - **200 OK** with `{ "success": true, "missingItems": {} }` if all ingredients are available and reserved.
    - **409 CONFLICT** with `{ "success": false, "missingItems": { ... } }` detailing which ingredients are short.

### Kitchen Service (via API Gateway, port 8080)

- **POST** `http://localhost:8080/api/kitchen/orders`  
  - **Description**: Create a new kitchen order (called by `orderservice` after order creation).
  - **Example body**:
    ```json
    {
      "orderId": 1,
      "cakeName": "Standard cake"
    }
    ```
  - **Behaviour**: Saves a kitchen order with status `IN_PROGRESS` and immediately notifies `orderservice` so the main order status is kept in sync.

- **GET** `http://localhost:8080/api/kitchen/orders`  
  - **Description**: List all kitchen orders (for **Chef**/**Staff** views).

- **PATCH** `http://localhost:8080/api/kitchen/orders/{kitchenOrderId}/ready`  
  - **Description**: Used by the **Chef** to mark a cake as ready. Updates local status to `READY` and pushes the change to `orderservice`.

- **PATCH** `http://localhost:8080/api/kitchen/orders/{kitchenOrderId}/delivered`  
  - **Description**: Used by **Staff** to mark a cake as delivered. Updates local status to `DELIVERED` and pushes the change to `orderservice`.

---

## Postman Collection

A complete Postman collection is provided at:

- **`SmartCakeShop-microservices.postman_collection.json`**

It contains requests for:

- Seeding and listing inventory.
- Checking and reserving ingredients (success and insufficient stock edge cases).
- Creating orders (happy path and failure when inventory is not enough).
- Listing and retrieving orders.
- Manually changing order status.
- Listing kitchen orders and driving them through `READY` and `DELIVERED` states (Chef/Staff actions), verifying that order status is updated via inter-service calls.

You can import this file into Postman and run the requests directly against the services started with Docker Compose.

---

## Notes

- Only the **three required services** are implemented in the microservices layer: `orderservice`, `inventoryservice`, and `kitchenservice`. There is **no** notification, reporting, or auth microservice in this implementation, as requested.
- The legacy `SmartCakeShop` folder shows the original monolithic Java design patterns (Factory, Builder, Observer, Singleton); the new microservices reuse the same ideas while adding **service boundaries**, **separate databases**, and **Docker-based deployment**.
