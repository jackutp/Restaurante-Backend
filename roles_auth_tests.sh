#!/bin/bash

BASE_URL="http://localhost:8080/api/usuarios"

echo "========================================"
echo " LOGIN ADMINISTRADOR (Juan Perez)"
echo "========================================"

ADMIN_LOGIN=$(curl -s -X POST "$BASE_URL/login" \
	-H "Content-Type: application/json" \
	-d '{
    "email": "nuevo@example.com",
    "password": "abc123"
}')

echo "$ADMIN_LOGIN" | jq

ADMIN_TOKEN=$(echo "$ADMIN_LOGIN" | jq -r '.token')

echo -e "\n========================================"
echo " TOKEN ADMIN"
echo "========================================"

echo "$ADMIN_TOKEN"

echo -e "\n========================================"
echo " ADMIN LISTA USUARIOS"
echo " DEBERÍA FUNCIONAR"
echo "========================================"

curl -s -X GET "$BASE_URL" \
	-H "Authorization: Bearer $ADMIN_TOKEN" | jq

echo -e "\n========================================"
echo " SE CREA NUEVO CLIENTE"
echo "========================================"

curl -s -X POST "$BASE_URL/registro" \
	-H "Content-Type: application/json" \
	-d '{
    "nombre": "Carlos",
    "apellido": "Ramirez",
    "dni": "87654321",
    "email": "carlos@example.com",
    "clave": "654321"
}' | jq

echo -e "\n========================================"
echo " LOGIN CLIENTE (Carlos)"
echo "========================================"

CLIENT_LOGIN=$(curl -s -X POST "$BASE_URL/login" \
	-H "Content-Type: application/json" \
	-d '{
    "email": "carlos@example.com",
    "password": "654321"
}')

echo "$CLIENT_LOGIN" | jq

CLIENT_TOKEN=$(echo "$CLIENT_LOGIN" | jq -r '.token')

echo -e "\n========================================"
echo " TOKEN CLIENTE"
echo "========================================"

echo "$CLIENT_TOKEN"

echo -e "\n========================================"
echo " CLIENTE INTENTA LISTAR USUARIOS"
echo " DEBERÍA RESPONDER 403"
echo "========================================"

curl -i -X GET "$BASE_URL" \
	-H "Authorization: Bearer $CLIENT_TOKEN"

echo -e "\n========================================"
echo " CLIENTE INTENTA BUSCAR SU PROPIO USUARIO"
echo " DEBERÍA RESPONDER 200"
echo "========================================"

curl -i -X GET "$BASE_URL/4" \
	-H "Authorization: Bearer $CLIENT_TOKEN"

echo -e "\n========================================"
echo " CLIENTE INTENTA ACTUALIZAR USUARIO"
echo " DEBERÍA RESPONDER 403"
echo "========================================"

curl -i -X PUT "$BASE_URL/1?tipo=ADMINISTRADOR" \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $CLIENT_TOKEN" \
	-d '{
    "nombre": "Hack",
    "apellido": "Hack",
    "dni": "11111111",
    "email": "hack@example.com",
    "clave": "hack123"
}'

echo -e "\n========================================"
echo " ADMIN ACTUALIZA CLIENTE"
echo " DEBERÍA FUNCIONAR"
echo "========================================"

curl -s -X PUT "$BASE_URL/4?tipo=CLIENTE" \
	-H "Content-Type: application/json" \
	-H "Authorization: Bearer $ADMIN_TOKEN" \
	-d '{
    "nombre": "Carlos Actualizado",
    "apellido": "Ramirez",
    "dni": "87654321",
    "email": "carlosnuevo@example.com",
    "clave": "newpass123"
}' | jq

echo -e "\n========================================"
echo " ADMIN ELIMINA CLIENTE"
echo "========================================"

curl -i -X DELETE "$BASE_URL/4" \
	-H "Authorization: Bearer $ADMIN_TOKEN"

echo -e "\n========================================"
echo " FIN DE PRUEBAS"
echo "========================================"
