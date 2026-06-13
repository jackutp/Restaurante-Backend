import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },   // Calentamiento
        { duration: '1m', target: 100 },   // Carga normal
        { duration: '2m', target: 200 },   // Pico máximo
        { duration: '30s', target: 0 },    // Enfriamiento
    ],
    thresholds: {
        'http_req_duration': ['p(95)<2000'],  // 95% bajo 2 segundos
        'http_req_failed': ['rate<0.01'],     // Menos de 1% errores
    },
};

export default function () {
    // PRUEBA 1: Endpoint sin paginación (PELIGROSO)
    let res1 = http.get('http://localhost:8083/productos/all');
    check(res1, {
        'GET /all status 200': (r) => r.status === 200,
        'GET /all tiempo < 1s': (r) => r.timings.duration < 1000,
    });

    // PRUEBA 2: Endpoint por ID (seguro)
    let randomId = Math.floor(Math.random() * 100) + 1;
    let res2 = http.get(`http://localhost:8083/productos/${randomId}`);
    check(res2, {
        'GET /{id} status 200 o 404': (r) => r.status === 200 || r.status === 404,
    });

    // PRUEBA 3: Crear producto (solo 10% de las veces para no llenar BD)
    if (__VU % 10 === 0) {
        const payload = {
            nombre: `Producto Test ${Date.now()}`,
            descripcion: "Prueba de estrés",
            precio: 99.99,
            categoria: "PLATO"
        };
        let res3 = http.post('http://localhost:8083/productos/crear', payload, {
            headers: { 'Content-Type': 'application/json' }
        });
        check(res3, {
            'POST /crear status 201': (r) => r.status === 201,
        });
    }

    sleep(1);
}