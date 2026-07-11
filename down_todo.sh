for svc in \
eureka \
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
    railway down --service "$svc" -y
done