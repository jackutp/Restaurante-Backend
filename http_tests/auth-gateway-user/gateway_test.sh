#!/bin/bash

BASE_URL="http://localhost:8090"

echo "========================================"
echo "1. REGISTER USER"
echo "========================================"

curl -i -X POST "$BASE_URL/api/usuarios/registro" \
	-H "Content-Type: application/json" \
	-d '{
  "nombre":"Juan",
  "apellido":"Perez",
  "dni":"12345678",
  "email":"juan@test.com",
  "clave":"123456"
}'

echo ""
echo ""
echo "========================================"
echo "2. LOGIN USER"
echo "========================================"

LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/usuarios/login" \
	-H "Content-Type: application/json" \
	-d '{
  "email":"juan@test.com",
  "clave":"123456"
}')

echo "$LOGIN_RESPONSE"

echo ""
echo ""
echo "========================================"
echo "3. EXTRACT TOKEN"
echo "========================================"

TOKEN=$(echo "$LOGIN_RESPONSE" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

echo "TOKEN:"
echo "$TOKEN"

echo ""
echo ""
echo "========================================"
echo "4. ACCESS ADMIN ENDPOINT WITHOUT TOKEN"
echo "EXPECTED: 401"
echo "========================================"

curl -i "$BASE_URL/api/usuarios"

echo ""
echo ""
echo "========================================"
echo "5. ACCESS ADMIN ENDPOINT WITH CLIENTE TOKEN"
echo "EXPECTED: 403"
echo "========================================"

curl -i "$BASE_URL/api/usuarios" \
	-H "Authorization: Bearer $TOKEN"

echo ""
echo ""
echo "========================================"
echo "6. ACCESS PUBLIC PRODUCTOS"
echo "EXPECTED: 200"
echo "========================================"

curl -i "$BASE_URL/api/productos"

echo ""
echo ""
echo "========================================"
echo "7. TRY PRODUCT WRITE WITHOUT TOKEN"
echo "EXPECTED: 401"
echo "========================================"

curl -i -X POST "$BASE_URL/api/productos" \
	-H "Content-Type: application/json" \
	-d '{}'

echo ""
echo ""
echo "========================================"
echo "END TESTS"
echo "========================================"
