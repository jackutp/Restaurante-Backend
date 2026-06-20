package com.Gateway.Server.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    private JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        if (!jwtService.isTokenValid(token)) {
            return Mono.error(new BadCredentialsException("Invalid token"));
        }
        String email = jwtService.getUsernameFromToken(token);
        List<SimpleGrantedAuthority> authorities = jwtService.getAuthorities(token)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        return Mono.just(new UsernamePasswordAuthenticationToken(email, token, authorities));
    }
}
