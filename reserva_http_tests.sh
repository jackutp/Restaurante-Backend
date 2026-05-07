#!/bin/bash

BASE_URL="http://localhost:8082/api/reservas"

echo "==================================="
echo "CREATE RESERVA"
echo "==================================="

curl -s -X POST "$BASE_URL/create" \
	-H "Content-Type: application/json" \
	-d '{
  "mesaFk": 3,
  "clienteId": 2,
  "cantidadClientes": 4,
  "fecha": "2026-05-24",
  "hora": "15:30:00",
  "menu": "CARTA",
  "detalles": "Mesa cerca de ventana"
}'

echo ""
echo ""
echo "==================================="
echo "GET ALL RESERVAS"
echo "==================================="

curl -s -X GET "$BASE_URL/all" | jq

echo ""
echo ""
echo "==================================="
echo "GET RESERVA BY ID"
echo "==================================="

curl -s -X GET "$BASE_URL/search/1"

echo ""
echo ""
echo "==================================="
echo "UPDATE RESERVA"
echo "==================================="

curl -s -X PATCH "$BASE_URL/update/1" \
	-H "Content-Type: application/json" \
	-d '{
  "mesaFk": 4,
  "clienteId": 2,
  "cantidadClientes": 6,
  "fecha": "2026-05-24",
  "hora": "20:00:00",
  "menu": "DEGUSTACION",
  "detalles": "Actualizar reserva"
}'

echo ""
echo ""
echo "==================================="
echo "DELETE RESERVA"
echo "==================================="

curl -s -X DELETE "$BASE_URL/delete/1"

echo ""
echo ""
echo "==================================="
echo "DONE"
echo "==================================="
