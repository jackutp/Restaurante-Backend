package com.Gateway.Server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
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
   public AuthenticationWebFilter jwtAuthenticationWebFilter(JwtAuthenticationManager authManager){
       AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
       filter.setServerAuthenticationConverter(exchange -> {
           System.out.println("AUTH FILTER EXECUTED");
           String authHeader = exchange
                   .getRequest()
                   .getHeaders()
                   .getFirst(HttpHeaders.AUTHORIZATION);
           System.out.println("Header: " + authHeader);
           if(authHeader == null || !authHeader.startsWith("Bearer ")){
               return Mono.empty();
           }
           String token = authHeader.substring(7);
           return Mono.just(new UsernamePasswordAuthenticationToken(null, token));
       });
       filter.setAuthenticationFailureHandler((webFilterExchange, exception) ->{
           webFilterExchange.getExchange()
                   .getResponse()
                   .setStatusCode(HttpStatus.UNAUTHORIZED);
           return webFilterExchange.getExchange()
                   .getResponse()
                   .setComplete();
               }
               );
       return filter;
   }

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/usuarios/login",
            "/api/usuarios/registro"
    };
    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/productos/**"
    };
    private static final String[] ADMIN_ENDPOINTS = {
            "/api/insumos/**",
            "/api/mermas/**",
            "/api/proveedores/**"
    };
    private static final String[] ADMIN_CLIENTE_ENDPOINTS = {
            "/api/eventos/**",
            "/api/reservas/**"
    };
    private static final  String[] MESERO_ENDPOINTS = {
            //Aquí van los pagos, etc
    };
    private static final String[] COCINERO_MESERO_ENDPOINTS = {
            "/api/pedidos/**"
    };
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationManager authManager){
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(
                        jwtAuthenticationWebFilter(authManager),
                        SecurityWebFiltersOrder.AUTHENTICATION
                )
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((swe, e) ->
                {
                    swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return swe.getResponse().setComplete();
                }))
                .authorizeExchange(exchange -> exchange
                        //CORS
                        .pathMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()
                        //Publicos
                        .pathMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                        .permitAll()
                        //Catálogo productos público
                        .pathMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS)
                        .permitAll()
                        //Admin
                        .pathMatchers(HttpMethod.GET, "/api/usuarios/**")
                        .hasAuthority("ADMINISTRADOR")
                        .pathMatchers(HttpMethod.PUT, "/api/usuarios/**")
                        .hasAuthority("ADMINISTRADOR")
                        .pathMatchers(HttpMethod.DELETE, "/api/usuarios/**")
                        .hasAuthority("ADMINISTRADOR")
                        .pathMatchers(ADMIN_ENDPOINTS)
                        .hasAuthority("ADMINISTRADOR")
                        //Cliente/admin
                        .pathMatchers(ADMIN_CLIENTE_ENDPOINTS)
                        .hasAnyAuthority("CLIENTE", "ADMINISTRADOR")
                        //Mesero
                        .pathMatchers(COCINERO_MESERO_ENDPOINTS)
                        .hasAnyAuthority("MESERO", "COCINERO", "ADMINISTRADOR")
                        //Default
                        .anyExchange()
                        .authenticated()
                )
                .build();

    }
}
