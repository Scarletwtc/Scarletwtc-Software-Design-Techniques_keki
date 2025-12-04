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

## Postman Collection

A complete Postman collection is provided at:
- `change_the_name_of_the_collection.postman_collection.json` (repo root)

It contains requests for:
- Inventory: list all ingredients, seed/update single ingredient, check & reserve ingredients.
- Orders: create standard cake order, list all orders, get order by id.
- Kitchen: create kitchen order, list all kitchen orders, mark orders as `READY` and `DELIVERED`.

You can import this file into Postman and run the requests directly against the services started with Docker Compose.


### How to Import the Postman Collection

- Open Postman.
- Click `Import` → choose `File`.
- Select `change_the_name_of_the_collection.postman_collection.json` from the repo root.
- Click `Import`.
- Ensure Docker services are running:
  - `orderservice`: `http://localhost:8081`
  - `inventoryservice`: `http://localhost:8082`
  - `kitchenservice`: `http://localhost:8083`
- Recommended order to run:
  - Inventory: seed/list, then check-and-reserve.
  - Orders: create/list/detail; also test insufficient stock edge case.
  - Kitchen: create ticket, set `READY` then `DELIVERED`; confirm `orderservice` status updates.

Optional Postman environment:
- Add variables for convenience:
  - `order_base` = `http://localhost:8081`
  - `inventory_base` = `http://localhost:8082`
  - `kitchen_base` = `http://localhost:8083`
- Use `{{order_base}}`, `{{inventory_base}}`, `{{kitchen_base}}` in request URLs.

## Notes

- Only the **three required services** are implemented in the microservices layer: `orderservice`, `inventoryservice`, and `kitchenservice`. There is **no** notification, reporting, or auth microservice in this implementation, as requested.
- The legacy `SmartCakeShop` folder shows the original monolithic Java design patterns (Factory, Builder, Observer, Singleton); the new microservices reuse the same ideas while adding **service boundaries**, **separate databases**, and **Docker-based deployment**.
