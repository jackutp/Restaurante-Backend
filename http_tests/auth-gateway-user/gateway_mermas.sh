#!/bin/bash

BASE_URL="http://localhost:8090"

echo "========================================"
echo "1. LOGIN"
echo "========================================"

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/usuarios/login" \
	-H "Content-Type: application/json" \
	-d '{
    "email":"nuevo@example.com",
    "password":"abc123"
}')

echo "$LOGIN_RESPONSE"

echo ""
echo "========================================"
echo "2. EXTRACT TOKEN"
echo "========================================"

TOKEN=$(echo "$LOGIN_RESPONSE" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

echo "$TOKEN"

echo ""
echo "========================================"
echo "3. GET ALL MERMAS"
echo "EXPECTED: 200"
echo "========================================"

curl -i -X GET "$BASE_URL/api/mermas" \
	-H "Authorization: Bearer $TOKEN"

echo ""
echo ""
echo "========================================"
echo "4. GET MERMA BY ID"
echo "EXPECTED: 200 or 404"
echo "========================================"

curl -i -X GET "$BASE_URL/api/mermas/1" \
	-H "Authorization: Bearer $TOKEN"

echo ""
echo ""
echo "========================================"
echo "5. GET MERMAS BY TIPO"
echo "EXPECTED: 200"
echo "========================================"

curl -i -X GET "$BASE_URL/api/mermas/tipo/VENCIMIENTO" \
	-H "Authorization: Bearer $TOKEN"

echo ""
echo ""
echo "========================================"
echo "6. GET PRODUCTOS"
echo "EXPECTED: 200"
echo "========================================"

curl -i -X GET "$BASE_URL/api/mermas/productos" \
	-H "Authorization: Bearer $TOKEN"

echo ""
echo ""
echo "========================================"
echo "7. GET INSUMOS"
echo "EXPECTED: 200"
echo "========================================"

curl -i -X GET "$BASE_URL/api/mermas/insumos" \
	-H "Authorization: Bearer $TOKEN"

echo ""
echo ""
echo "========================================"
echo "END TESTS"
echo "========================================"
