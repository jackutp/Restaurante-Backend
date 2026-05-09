#!/bin/bash

BASE_URL="http://localhost:8080/api/usuarios"

echo "===== CREAR USUARIO ====="

curl -v -X POST "$BASE_URL/registro" \
	-H "Content-Type: application/json" \
	-d '{
    "nombre": "Juan",
    "apellido": "Perez",
    "dni": "12345678",
    "email": "juan@example.com",
    "clave": "123456"
}'

echo -e "\n\n===== LOGIN ====="

LOGIN_RESPONSE=$(curl -v -X POST "$BASE_URL/login" \
	-H "Content-Type: application/json" \
	-d '{
    "email": "juan@example.com",
    "password": "123456"
}')

echo "$LOGIN_RESPONSE"

TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')

echo -e "\n\n===== TOKEN OBTENIDO ====="
echo "$TOKEN"

echo -e "\n\n===== LISTAR USUARIOS ====="

curl -v -X GET "$BASE_URL" \
	-H "Authorization: Bearer $TOKEN"

echo -e "\n\n===== BUSCAR USUARIO ID 1 ====="

curl -v -X GET "$BASE_URL/1" \
	-H "Authorization: Bearer $TOKEN"

echo -e "\n\n===== ACTUALIZAR USUARIO ====="

curl v -X PUT "$BASE_URL/1?tipo=ADMINISTRADOR" \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $TOKEN" \
	-d '{
    "nombre": "Juan Actualizado",
    "apellido": "Perez",
    "dni": "12345678",
    "email": "nuevo@example.com",
    "clave": "abc123"
}'

#echo -e "\n\n===== ELIMINAR USUARIO ====="

#curl -i -X DELETE "$BASE_URL/1" \
#	-H "Authorization: Bearer $TOKEN"

echo -e "\n"
