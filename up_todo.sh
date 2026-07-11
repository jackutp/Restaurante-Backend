#!/bin/bash
railway up -d --service eureka
echo "Waiting for Eureka..."
until curl -fs https://eureka-production-eb9b.up.railway.app/actuator/health >/dev/null; do
    sleep 5
done
echo "Eureka is ready."
for svc in \
gateway \
users \
proveedor \
cocina \
cambios \
pedidos \
insumos \
eventos \
pagos \
mesas \
mermas \
producto \
reservas \
solicitudes
do
    railway up -d --service "$svc"
done