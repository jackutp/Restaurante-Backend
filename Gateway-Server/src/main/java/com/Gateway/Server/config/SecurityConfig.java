
package com.Gateway.Server.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;
import com.Gateway.Server.security.JwtAuthenticationManager;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(JwtAuthenticationManager authManager) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
        filter.setServerAuthenticationConverter(exchange -> {
            System.out.println("AUTH FILTER EXECUTED");
            String authHeader = exchange
                    .getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);
            System.out.println("Header: " + authHeader);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Mono.empty();
            }
            String token = authHeader.substring(7);
            return Mono.just(new UsernamePasswordAuthenticationToken(null, token));
        });
        filter.setAuthenticationFailureHandler((webFilterExchange, exception) -> {
            webFilterExchange.getExchange()
                    .getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);
            return webFilterExchange.getExchange()
                    .getResponse()
                    .setComplete();
        });
        return filter;
    }
    // ─────────────────────────────────────────────
    // ENDPOINTS COMPLETAMENTE PÚBLICOS (sin token)
    // ─────────────────────────────────────────────
    /** POST públicos: login, registro y creación de eventos/reservas desde el landing */
    private static final String[] PUBLIC_POST_ENDPOINTS = {
            "/api/usuarios/login",
            "/api/usuarios/registro",
            "/api/eventos",        // Formulario de cotización público (landing)
            "/api/reservas"        // Formulario de reserva público (landing)
    };
    /** GET públicos: catálogo de productos visible sin autenticación */
    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/productos/**"
    };
    // ─────────────────────────────────────────────
    // ENDPOINTS POR ROL
    // ─────────────────────────────────────────────
    /** Solo ADMINISTRADOR: gestión de inventario, mermas, proveedores y usuarios */
    private static final String[] ADMIN_ONLY_ENDPOINTS = {
            "/api/insumos/**",
            "/api/mermas/**",
            "/api/proveedores/**",
            "/api/usuarios/**"
    };

    /**
     * MESERO + ADMINISTRADOR: gestión de mesas y pagos.
     * El mesero necesita consultar y actualizar mesas, y procesar cobros.
     */
    private static final String[] MESERO_ADMIN_ENDPOINTS = {
            "/api/pagos/**",
            "/api/mesas/**"
    };

    /**
     * MESERO + COCINERO + ADMINISTRADOR: pedidos y vista de cocina.
     * Ambos roles operan sobre el flujo de órdenes.
     */
    private static final String[] COCINA_MESERO_ADMIN_ENDPOINTS = {
            "/api/pedidos/**",
            "/api/cocina/**"
    };

    /**
     * Todos los roles autenticados: ver y gestionar reservas y eventos.
     * Los meseros necesitan ver reservas del día; administradores gestionan todo.
     * Nota: el POST público a /api/eventos y /api/reservas ya está cubierto arriba.
     */
    private static final String[] ALL_AUTHENTICATED_ENDPOINTS = {
            "/api/reservas/**",
            "/api/eventos/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationManager authManager) {

        return http
                // Deshabilitar seguridad por defecto (la gestiona JWT)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)

                // Filtro JWT
                .addFilterAt(
                        jwtAuthenticationWebFilter(authManager),
                        SecurityWebFiltersOrder.AUTHENTICATION
                )

                // Manejo de excepciones de autenticación
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((swe, e) -> {
                            swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return swe.getResponse().setComplete();
                        })
                )

                // ── Reglas de autorización (orden importa: más específico primero) ──
                .authorizeExchange(exchange -> exchange

                        // 1. Preflight CORS siempre libre
                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // 2. POST públicos (login, registro, cotizaciones, reservas desde landing)
                        .pathMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS)
                        .permitAll()

                        // 3. GET públicos (catálogo de productos)
                        .pathMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
                        .permitAll()

                        // 4. Solo ADMINISTRADOR
                        .pathMatchers(ADMIN_ONLY_ENDPOINTS)
                        .hasAuthority("ADMINISTRADOR")

                        // 5. MESERO + ADMINISTRADOR (mesas y pagos)
                        .pathMatchers(MESERO_ADMIN_ENDPOINTS)
                        .hasAnyAuthority("MESERO", "ADMINISTRADOR")

                        // 6. MESERO + COCINERO + ADMINISTRADOR (pedidos y cocina)
                        .pathMatchers(COCINA_MESERO_ADMIN_ENDPOINTS)
                        .hasAnyAuthority("MESERO", "COCINERO", "ADMINISTRADOR")

                        // 7. Cualquier usuario autenticado (reservas y eventos protegidos)
                        .pathMatchers(ALL_AUTHENTICATED_ENDPOINTS)
                        .authenticated()

                        // 8. Cualquier otro endpoint requiere autenticación
                        .anyExchange()
                        .authenticated()
                )
                .build();
    }
}
