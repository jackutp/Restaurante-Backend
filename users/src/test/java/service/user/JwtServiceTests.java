package service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import service.user.jwt.JwtService;
import service.user.model.TipoUser;
import service.user.model.User;
import service.user.repository.UserRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("tests")
public class JwtServiceTests {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User createUser() {
        return userRepository.save(User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("23412354")
                .email("kenji_marruecos@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.ADMINISTRADOR)
                .build());
    }

    @Test
    void shouldGenerateValidJwtToken() {
        User user = createUser();
        String token = jwtService.getToken(user);
        Assertions.assertThat(token).isNotBlank();
        Assertions.assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void shouldExtractUsernameFromToken() {
        User user = createUser();
        String token = jwtService.getToken(user);
        String username = jwtService.getUsernameFromToken(token);
        Assertions.assertThat(username).isEqualTo("kenji_marruecos@test.com");
    }

    @Test
    void shouldValidateCorrectToken() {
        User user = createUser();
        String token = jwtService.getToken(user);
        boolean valid = jwtService.isTokenValid(token, user);
        Assertions.assertThat(valid).isTrue();
    }

    @Test
    void shouldRejectTamperedToken() {
        Assertions.assertThatThrownBy(() -> jwtService.isTokenValid("bad.token.here", null)).isInstanceOf(io.jsonwebtoken.JwtException.class);
    }
}
