#!/bin/bash

BASE_URL="http://localhost:8001/api/ingredientes"

echo "==============================="
echo "TEST GET INGREDIENTES"
echo "==============================="

curl -s -X GET "$BASE_URL" | jq

echo ""
echo "==============================="
echo "DONE"
echo "==============================="
