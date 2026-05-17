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
