package com.Gateway.Server;

import com.Gateway.Server.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@ActiveProfiles("local")
@SpringBootTest
class GatewayServerApplicationTests {

	private JwtService jwtService;
	@Value("${jwt.secret}")
	private String SECRET_KEY;

	@BeforeEach
	void setUp(){
		jwtService = new JwtService();
		ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET_KEY);
	}

	private String generateToken(String username, List<String> authorities, Date expiration){
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		SecretKey key = Keys.hmacShaKeyFor(keyBytes);
		return Jwts.builder()
				.subject(username)
				.claim("authorities", authorities)
				.issuedAt(new Date())
				.expiration(expiration)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
	@Test
	void contextLoads() {
	}

	@Test
	void shouldExtractUsernameFromToken() {
		String token = generateToken("kenji@test.com", List.of("ADMINISTRADOR"),
				new Date(System.currentTimeMillis() + 100000)
		);
		String username = jwtService.getUsernameFromToken(token);
		Assertions.assertThat(username).isEqualTo("kenji@test.com");
	}

	@Test
	void shouldExtractAuthorities() {
		String token = generateToken("kenji@test.com", List.of("ADMINISTRADOR", "MESERO"),
				new Date(System.currentTimeMillis() + 100000)
		);
		List<String> authorities = jwtService.getAuthorities(token);
		Assertions.assertThat(authorities) .containsExactly("ADMINISTRADOR", "MESERO");
	}
	@Test
	void shouldValidateCorrectToken() {
		String token = generateToken( "kenji@test.com", List.of("CLIENTE"),
				new Date(System.currentTimeMillis() + 100000)
		);
		boolean valid = jwtService.isTokenValid(token);
		Assertions.assertThat(valid).isTrue();
	}

	@Test
	void shouldRejectExpiredToken() {
		String token = generateToken("kenji@test.com", List.of("CLIENTE"),
				new Date(System.currentTimeMillis() - 100000)
		);
		boolean valid = jwtService.isTokenValid(token);
		Assertions.assertThat(valid).isFalse();
	}

	@Test
	void shouldRejectMalformedToken() {
		boolean valid = jwtService.isTokenValid("bad.token.here");

		Assertions.assertThat(valid).isFalse();
	}

}
