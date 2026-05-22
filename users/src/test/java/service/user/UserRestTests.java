package service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import service.user.dto.UserLoginRequestDTO;
import service.user.model.User;
import service.user.model.TipoUser;
import service.user.dto.UserRegistroDTO;
import service.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public class UserRestTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserRegistroDTO buildRegistroDTO() {
        return new UserRegistroDTO(
                "Kenji",
                "Perez",
                "44411122",
                "kenji@test.com",
                "password123"
        );
    }

    private UserLoginRequestDTO buildLoginDTO() {
        return new UserLoginRequestDTO(
                "kenji@test.com",
                "password123"
        );
    }

    @BeforeEach
    void setup() {
        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = User.builder()
                    .nombre("Admin")
                    .apellido("Principal")
                    .dni("99999999")
                    .email("admin@test.com")
                    .clave(passwordEncoder.encode("admin123"))
                    .tipo(TipoUser.ADMINISTRADOR)
                    .build();
            userRepository.save(admin);
        }
    }
    @Test
    void shouldRegisterUser() throws Exception {
        UserRegistroDTO dto = buildRegistroDTO();
        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").exists())
                .andExpect(jsonPath("$.nombre").value("Kenji"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.email").value("kenji@test.com"))
                .andExpect(jsonPath("$.tipo").value("CLIENTE"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldLoginUser() throws Exception {
        User user = User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("44411122")
                .email("kenji@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.CLIENTE)
                .build();
        userRepository.save(user);
        UserLoginRequestDTO loginDTO = buildLoginDTO();
        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("kenji@test.com"))
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        User user = User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("44411122")
                .email("kenji@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.CLIENTE)
                .build();
        userRepository.save(user);
        UserLoginRequestDTO badLogin = new UserLoginRequestDTO(
                "kenji@test.com",
                "wrongpassword"
        );
        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        User user = User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("44411122")
                .email("kenji@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.CLIENTE)
                .build();
        userRepository.save(user);
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldGetUserById() throws Exception {
        User user = User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("44411122")
                .email("kenji@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.CLIENTE)
                .build();
        User saved = userRepository.save(user);
        mockMvc.perform(get("/usuarios/" + saved.getIdUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        User user = User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("44411122")
                .email("kenji@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.CLIENTE)
                .build();
        User saved = userRepository.save(user);
        UserRegistroDTO updatedDTO = new UserRegistroDTO(
                "Mario",
                "Gomez",
                "66611122",
                "mario@test.com",
                "newpassword123"
        );
        mockMvc.perform(put("/usuarios/" + saved.getIdUsuario())
                        .param("tipo", "ADMINISTRADOR")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        User user = User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("44411122")
                .email("kenji@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.CLIENTE)
                .build();
        User saved = userRepository.save(user);
        mockMvc.perform(delete("/usuarios/" + saved.getIdUsuario()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestWhenRegisteringInvalidUser() throws Exception {
        UserRegistroDTO invalidDTO = new UserRegistroDTO(
                "",
                "",
                "123",
                "bad-email",
                "123"
        );
        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        User existing = User.builder()
                .nombre("Kenji")
                .apellido("Perez")
                .dni("44411122")
                .email("kenji@test.com")
                .clave(passwordEncoder.encode("password123"))
                .tipo(TipoUser.CLIENTE)
                .build();
        userRepository.save(existing);
        UserRegistroDTO dto = buildRegistroDTO();
        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/usuarios/99999")).andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoCredentialsProvidedToProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/usuarios")).andExpect(status().isForbidden());
        mockMvc.perform(get("/usuarios/1")).andExpect(status().isForbidden());
        mockMvc.perform(delete("/usuarios/1")).andExpect(status().isForbidden());
    }
}
