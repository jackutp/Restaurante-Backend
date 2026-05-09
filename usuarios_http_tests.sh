#!/bin/bash

BASE_URL="http://localhost:8080/api/usuarios"

echo "===== CREAR USUARIO ====="

curl -X POST "$BASE_URL/registro" \
	-H "Content-Type: application/json" \
	-d '{
  "nombre": "Juan",
  "apellido": "Perez",
  "dni": "12345678",
  "email": "juan@example.com",
  "clave": "123456"
}' | jq

echo -e "\n\n===== LISTAR USUARIOS ====="

curl -X GET "$BASE_URL" | jq

echo -e "\n\n===== BUSCAR USUARIO ID 1 ====="

curl -X GET "$BASE_URL/1" | jq

echo -e "\n\n===== ACTUALIZAR USUARIO ====="

curl -X PUT "$BASE_URL/1?tipo=ADMINISTRADOR" \
	-H "Content-Type: application/json" \
	-d '{
  "nombre": "Juan Actualizado",
  "apellido": "Perez",
  "dni": "12345678",
  "email": "nuevo@example.com",
  "clave": "abc123"
}'

echo -e "\n\n===== ELIMINAR USUARIO ====="

curl -X DELETE "$BASE_URL/1"

echo -e "\n"
