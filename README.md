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
- Exposes a **REST API** and communicates asynchronously via **RabbitMQ message queue**.
- Is packaged with a dedicated **Dockerfile** and wired together via `docker-compose.yml`.

The original Java desktop prototype with design patterns remains in the `SmartCakeShop` folder for reference; the microservices implementation reuses those ideas in a distributed architecture.

---

## Message Queue Integration with RabbitMQ

The system uses **RabbitMQ** as a message broker to enable **asynchronous, event-driven communication** between services. This provides significant architectural benefits over direct HTTP calls.

### Architecture Overview

**RabbitMQ Components:**
- **Exchange**: `keki.exchange` (TopicExchange) - central message routing hub
- **Queues**:
  - `inventory.order-placed` - new orders requiring inventory validation
  - `order.inventory-result` - inventory check results
  - `kitchen.order-confirmed` - confirmed orders ready for kitchen preparation

### Asynchronous Event Flow

When a customer places an order, the following **asynchronous workflow** occurs:

1. **OrderService** saves the order to database and publishes `order.placed` event to RabbitMQ - Returns immediately to customer (non-blocking) - Event contains: orderId, cakeName, quantity

2. **InventoryService** listens on `inventory.order-placed` queue - Automatically receives the event via `@RabbitListener` - Checks ingredient stock (flour, sugar, eggs) - Publishes `inventory.result` event with success/failure status

3. **OrderService** listens on `order.inventory-result` queue - Receives inventory check result - If successful: updates order status to CONFIRMED, publishes `order.confirmed` event - If failed: updates order status to REJECTED

4. **KitchenService** listens on `kitchen.order-confirmed` queue - Receives confirmed orders - Creates kitchen order ticket - Updates order service with kitchen order ID

### Benefits of Message Queue Architecture

**A) Asynchronous Processing (Non-Blocking)**
- Services don't wait for responses - they publish events and continue immediately
- Customer receives instant confirmation without waiting for inventory checks
- Dramatically improves response times and throughput
- Example: OrderService can process 1000s of orders while InventoryService processes checks in background

**B) Scalability**
- Services can be scaled independently based on workload
- Multiple instances consume from the same queue, automatically load balancing
- Example: `docker compose up --scale inventoryservice=5` adds 5 workers sharing the queue
- Messages queue up during high load, preventing system overload

**C) Decoupling**
- Services don't need to know each other's locations or ports
- OrderService publishes events without knowing if/where InventoryService exists
- Services can be deployed, updated, or replaced independently
- Loose coupling enables independent development and deployment cycles

**D) Fault Tolerance**
- Messages persist in queues if services are temporarily down
- When services restart, they process all queued messages (no data loss)
- Durable queues survive RabbitMQ restarts
- Failed processing can be retried automatically
- Example: If InventoryService crashes, messages wait safely in queue

---

## CI/CD Pipeline (GitHub Actions)

A complete **Continuous Integration and Deployment** pipeline is configured in `.github/workflows/ci-cd.yml`. The pipeline automatically builds, tests, and deploys all services whenever code is pushed to the `5_message_queue` or `master` branch.

### Pipeline Overview

The pipeline consists of two main jobs:

**Job 1: Build and Test**
1. Checks out code from GitHub
2. Sets up JDK 17 with Maven caching
3. Builds all services (OrderService, InventoryService, KitchenService, API Gateway)
4. **Runs unit tests** that validate RabbitMQ message queue integration
5. Uploads JAR artifacts for deployment

**Job 2: Docker Build and Deploy**
1. Builds Docker images for all services
2. Starts entire system with `docker compose up -d` - 3 microservices + API Gateway - 3 PostgreSQL databases - RabbitMQ with management UI
3. Performs health checks on all services
4. **Runs end-to-end test**: Creates order, verifies async processing through message queue
5. On failure: automatically dumps container logs for debugging
6. Cleans up: `docker compose down -v`

### What the Tests Validate

The unit tests specifically verify **RabbitMQ integration**:
- **OrderService Tests**: Verify `order.placed` events are published to RabbitMQ
- **InventoryService Tests**: Verify inventory checks publish correct `inventory.result` events
- **KitchenService Tests**: Verify kitchen orders are created from `order.confirmed` events
- **Event Handler Tests**: Verify `@RabbitListener` methods correctly process incoming messages

### How to Observe the CI/CD Pipeline

**View Pipeline Runs:**
1. Go to GitHub repository
2. Click **"Actions"** tab
3. See all workflow runs with status
4. Click any run to see detailed logs

**Local Testing:**
```bash
# Run tests
cd orderservice && mvn test
cd ../inventoryservice && mvn test
cd ../kitchenservice && mvn test
```

### Pipeline Triggers

The pipeline automatically runs on:
- Every push to `5_message_queue` branch
- Every push to `master` branch  
- Every pull request to `master` branch
## Running Everything with Docker Compose

### Prerequisites

- **Docker** and **Docker Compose** installed.
- No other services already listening on ports **8080, 8081, 8082, 8083**, **5433, 5434, 5435**, **5672**, or **15672**.

### Start the system

From the repository root:

```bash
docker compose up --build
```

This will:

- Build and start:
  - `rabbitmq` on ports `5672` (AMQP) and `15672` (Management UI)
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
- The legacy `SmartCakeShop` folder shows the original monolithic Java design patterns (Factory, Builder, Observer, Singleton); the new microservices reuse the same ideas while adding **service boundaries**, **separate databases**, **Docker-based deployment**, and **asynchronous communication via RabbitMQ**.
