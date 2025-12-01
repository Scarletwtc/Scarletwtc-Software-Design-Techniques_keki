#!/usr/bin/env bash

# all-requests.sh
# --------------------------------------------------------------------
# This script demonstrates EVERY main API route exposed by the three
# microservices:
#   - inventoryservice
#   - orderservice
#   - kitchenservice
#   All routes are called through the API Gateway on port 8080.
#
# It prints the route being called, then the raw JSON response.
#
# Prerequisites:
#   - From the project root, Docker is running:
#       docker compose up --build
#
# NOTE:
#   - This script is idempotent enough for local testing, but re-running
#     will add more data (extra ingredients, more orders, etc.).

set -e

banner() {
  echo
  echo "=================================================================="
  echo "$1"
  echo "=================================================================="
}

call() {
  echo
  echo ">>> $1"
  shift
  "$@"
  echo
}

# -------------------------------------------------------------------
# INVENTORY SERVICE (through gateway: 8080)
# -------------------------------------------------------------------

banner "INVENTORY SERVICE - Seed ingredients (POST /api/inventory)"

call "POST /api/inventory (Flour)" \
  curl -s -X POST http://localhost:8080/api/inventory \
    -H "Content-Type: application/json" \
    -d '{"name":"Flour","quantity":100}'

call "POST /api/inventory (Sugar)" \
  curl -s -X POST http://localhost:8080/api/inventory \
    -H "Content-Type: application/json" \
    -d '{"name":"Sugar","quantity":100}'

call "POST /api/inventory (Egg)" \
  curl -s -X POST http://localhost:8080/api/inventory \
    -H "Content-Type: application/json" \
    -d '{"name":"Egg","quantity":100}'

banner "INVENTORY SERVICE - List all ingredients (GET /api/inventory)"

call "GET /api/inventory" \
  curl -s http://localhost:8080/api/inventory

banner "INVENTORY SERVICE - Check & reserve (success) (POST /api/inventory/check-and-reserve)"

call "POST /api/inventory/check-and-reserve (success)" \
  curl -s -X POST http://localhost:8080/api/inventory/check-and-reserve \
    -H "Content-Type: application/json" \
    -d '{
      "items": {
        "Flour": 2,
        "Sugar": 1,
        "Egg": 3
      }
    }'

banner "INVENTORY SERVICE - Check & reserve (insufficient) (POST /api/inventory/check-and-reserve)"

call "POST /api/inventory/check-and-reserve (insufficient)" \
  curl -s -X POST http://localhost:8080/api/inventory/check-and-reserve \
    -H "Content-Type: application/json" \
    -d '{
      "items": {
        "Flour": 100000
      }
    }'

# -------------------------------------------------------------------
# ORDER SERVICE (through gateway: 8080)
# -------------------------------------------------------------------

banner "ORDER SERVICE - Create order (happy path) (POST /api/orders)"

CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName":"Alice",
    "flavour":"CHOCOLATE",
    "color":"BROWN",
    "quantity":1
  }')

echo
echo ">>> POST /api/orders (happy path)"
echo "$CREATE_RESPONSE"

ORDER_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)

if [ -z "$ORDER_ID" ]; then
  echo "WARNING: Could not parse order id from happy-path response."
else
  echo "Parsed ORDER_ID from happy-path create: $ORDER_ID"
fi

banner "ORDER SERVICE - Create order (insufficient inventory) (POST /api/orders)"

call "POST /api/orders (insufficient inventory)" \
  curl -s -X POST http://localhost:8080/api/orders \
    -H "Content-Type: application/json" \
    -d '{
      "customerName":"Bob",
      "flavour":"VANILLA",
      "color":"WHITE",
      "quantity":9999
    }'

banner "ORDER SERVICE - List all orders (GET /api/orders)"

call "GET /api/orders" \
  curl -s http://localhost:8080/api/orders

if [ -n "$ORDER_ID" ]; then
  banner "ORDER SERVICE - Get order by id (GET /api/orders/{id})"

  call "GET /api/orders/${ORDER_ID}" \
    curl -s "http://localhost:8080/api/orders/${ORDER_ID}"

  banner "ORDER SERVICE - Manually update status (PATCH /api/orders/{id}/status)"

  call "PATCH /api/orders/${ORDER_ID}/status?status=CANCELLED" \
    curl -s -X PATCH "http://localhost:8080/api/orders/${ORDER_ID}/status?status=CANCELLED"
else
  echo
  echo "Skipping GET/PATCH by id because ORDER_ID could not be parsed."
fi

# -------------------------------------------------------------------
# KITCHEN SERVICE (through gateway: 8080)
# -------------------------------------------------------------------

banner "KITCHEN SERVICE - Create kitchen order manually (POST /api/kitchen/orders)"

call "POST /api/kitchen/orders (manual)" \
  curl -s -X POST http://localhost:8080/api/kitchen/orders \
    -H "Content-Type: application/json" \
    -d '{
      "orderId": 1,
      "cakeName": "Standard cake"
    }'

banner "KITCHEN SERVICE - List all kitchen orders (GET /api/kitchen/orders)"

KITCHEN_LIST=$(curl -s http://localhost:8080/api/kitchen/orders)

echo
echo ">>> GET /api/kitchen/orders"
echo "$KITCHEN_LIST"

KITCHEN_ORDER_ID=$(echo "$KITCHEN_LIST" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)

if [ -z "$KITCHEN_ORDER_ID" ]; then
  echo "WARNING: Could not parse any kitchen order id; skipping READY/DELIVERED calls."
  echo
  echo "All routes have otherwise been demonstrated."
  exit 0
fi

echo "Parsed KITCHEN_ORDER_ID: $KITCHEN_ORDER_ID"

banner "KITCHEN SERVICE - Mark kitchen order READY (PATCH /api/kitchen/orders/{id}/ready)"

call "PATCH /api/kitchen/orders/${KITCHEN_ORDER_ID}/ready" \
  curl -s -X PATCH "http://localhost:8080/api/kitchen/orders/${KITCHEN_ORDER_ID}/ready"

banner "KITCHEN SERVICE - Mark kitchen order DELIVERED (PATCH /api/kitchen/orders/{id}/delivered)"

call "PATCH /api/kitchen/orders/${KITCHEN_ORDER_ID}/delivered" \
  curl -s -X PATCH "http://localhost:8080/api/kitchen/orders/${KITCHEN_ORDER_ID}/delivered"

echo
echo "All main routes for the three microservices have now been exercised."


