#!/usr/bin/env bash

# Demo script: run one complete happy-path order through all three microservices.
# Prerequisites:
# - docker compose up --build is already running from the project root
# - Services listening on:
#     orderservice:      http://localhost:8081
#     inventoryservice:  http://localhost:8082
#     kitchenservice:    http://localhost:8083

set -e

echo "=== Step 1: Seeding inventory in inventoryservice ==="

# Add Flour
curl -s -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"name":"Flour","quantity":10}' >/dev/null

# Add Sugar
curl -s -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"name":"Sugar","quantity":10}' >/dev/null

# Add Egg
curl -s -X POST http://localhost:8082/api/inventory \
  -H "Content-Type: application/json" \
  -d '{"name":"Egg","quantity":30}' >/dev/null

echo "Inventory seeded (Flour, Sugar, Egg)."

echo
echo "=== Step 2: Creating a new order in orderservice ==="

# Create a standard chocolate cake order for Alice
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName":"Alice",
    "flavour":"CHOCOLATE",
    "color":"BROWN",
    "quantity":1
  }')

echo "Raw create-order response:"
echo "$CREATE_RESPONSE"

# Very simple parsing of the order id from the JSON
ORDER_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)

if [ -z "$ORDER_ID" ]; then
  echo "ERROR: Could not parse order id from response, aborting."
  exit 1
fi

echo "Parsed order id: $ORDER_ID"

echo
echo "=== Step 3: Waiting for kitchenservice to create the kitchen order ==="

# Give the system a moment to propagate and persist
sleep 2

echo "Fetching kitchen orders from kitchenservice..."
KITCHEN_LIST=$(curl -s http://localhost:8083/api/kitchen/orders)
echo "Raw kitchen orders list:"
echo "$KITCHEN_LIST"

# Pick the first kitchen order id from the list
KITCHEN_ORDER_ID=$(echo "$KITCHEN_LIST" | grep -o '"id":[0-9]*' | head -n1 | cut -d: -f2)

if [ -z "$KITCHEN_ORDER_ID" ]; then
  echo "ERROR: Could not find any kitchen order, aborting."
  exit 1
fi

echo "Parsed kitchen order id: $KITCHEN_ORDER_ID"

echo
echo "=== Step 4: Chef marks the kitchen order as READY ==="

curl -s -X PATCH "http://localhost:8083/api/kitchen/orders/${KITCHEN_ORDER_ID}/ready" >/dev/null
echo "Kitchen order $KITCHEN_ORDER_ID marked READY."

echo
echo "=== Step 5: Staff marks the kitchen order as DELIVERED ==="

curl -s -X PATCH "http://localhost:8083/api/kitchen/orders/${KITCHEN_ORDER_ID}/delivered" >/dev/null
echo "Kitchen order $KITCHEN_ORDER_ID marked DELIVERED."

echo
echo "=== Step 6: Fetch final order state from orderservice ==="

FINAL_ORDER=$(curl -s "http://localhost:8081/api/orders/${ORDER_ID}")
echo "Final order JSON:"
echo "$FINAL_ORDER"

echo
echo "Demo complete. The order status above should be \"DELIVERED\"."


