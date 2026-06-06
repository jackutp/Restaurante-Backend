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
import service.user.jwt.JwtService;
import service.user.model.TipoUser;
import service.user.dto.UserRegistroDTO;
import service.user.model.User;
import service.user.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("tests")
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

    @Autowired
    private JwtService jwtService;

    private User adminUser;
    private String adminToken;

    private User clienteUser;
    private String clienteToken;

    @BeforeEach
    void setUp() {

        adminUser = new User();
        adminUser.setNombre("Admin");
        adminUser.setApellido("Principal");
        adminUser.setDni("33334444");
        adminUser.setEmail("admin@test.com");
        adminUser.setClave(passwordEncoder.encode("admin123"));
        adminUser.setTipo(TipoUser.ADMINISTRADOR);

        userRepository.save(adminUser);

        adminToken = jwtService.getToken(adminUser);

        clienteUser = new User();
        clienteUser.setNombre("Cliente");
        clienteUser.setApellido("Normal");
        clienteUser.setDni("87654321");
        clienteUser.setEmail("cliente@test.com");
        clienteUser.setClave(passwordEncoder.encode("cliente123"));
        clienteUser.setTipo(TipoUser.CLIENTE);

        userRepository.save(clienteUser);

        clienteToken = jwtService.getToken(clienteUser);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void login_ShouldReturnToken() throws Exception {

        UserLoginRequestDTO request =
                new UserLoginRequestDTO(
                        "admin@test.com",
                        "admin123"
                );

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenPasswordIncorrect() throws Exception {
        UserLoginRequestDTO request =
                new UserLoginRequestDTO(
                        "admin@test.com",
                        "incorrecta"
                );
        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenEmailInvalid() throws Exception {

        UserLoginRequestDTO request =
                new UserLoginRequestDTO(
                        "correo-invalido",
                        "admin123"
                );

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registro_ShouldCreateUser() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "Juan",
                        "Perez",
                        "11112222",
                        "juan@test.com",
                        "password123"
                );

        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.dni").value("11112222"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.tipo").value("CLIENTE"));
    }

    @Test
    void registro_ShouldReturnBadRequest_WhenEmailInvalid() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "Juan",
                        "Perez",
                        "11112222",
                        "correo-invalido",
                        "password123"
                );

        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registro_ShouldReturnBadRequest_WhenDniInvalid() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "Juan",
                        "Perez",
                        "123",
                        "juan@test.com",
                        "password123"
                );

        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registro_ShouldReturnBadRequest_WhenPasswordTooShort() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "Juan",
                        "Perez",
                        "11112222",
                        "juan@test.com",
                        "123"
                );

        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodos_ShouldReturnUsers() throws Exception {

        mockMvc.perform(get("/usuarios/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /*
    This test is being commented-out since the token-authentication logic is being done by the gateway.
    A second layer of token handling MAY be added in the future. I'm NOT making any promises that this will be case.
    This is being commented-out and not deleted outright since, if I WERE to add this second layer, the test would be useful.
    @Test
    void listarTodos_ShouldReturnUnauthorized_WhenNoToken() throws Exception {

        mockMvc.perform(get("/usuarios/all"))
                .andExpect(status().isUnauthorized());
    }
     */

    @Test
    void buscarPorId_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/usuarios/" + adminUser.getIdUsuario())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void buscarPorId_ShouldReturnNotFound() throws Exception {

        mockMvc.perform(get("/usuarios/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizar_ShouldUpdateUser() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "AdminActualizado",
                        "Principal",
                        "99998888",
                        "adminupdated@test.com",
                        "password123"
                );

        mockMvc.perform(put("/usuarios/" + adminUser.getIdUsuario())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("AdminActualizado"))
                .andExpect(jsonPath("$.email").value("adminupdated@test.com"));
    }

    @Test
    void eliminar_ShouldDeleteUser() throws Exception {

        mockMvc.perform(delete("/usuarios/" + clienteUser.getIdUsuario())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void createUserByAdmin_ShouldCreateUser() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "Nuevo",
                        "Admin",
                        "99998888",
                        "nuevoadmin@test.com",
                        "password123"
                );

        mockMvc.perform(post("/usuarios/admin/create")
                        .param("tipo", "ADMINISTRADOR")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("nuevoadmin@test.com"))
                .andExpect(jsonPath("$.tipo").value("ADMINISTRADOR"));
    }

    /*
    This set of tests is no longer needed as anyone can create a user as a part of the registering process, but the role will always be "CLIENTE" by default.
    Therefore, limiting the creation of an account to the admin is redundant.
    However, the system admin can create AND modify the role of a user. This is useful for MESEROS and COCINEROS.
    Still, I'll not delete it outright just in case.
    @Test
    void createUserByAdmin_ShouldReturnForbidden_WhenUserIsNotAdmin() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "Nuevo",
                        "Usuario",
                        "44445555",
                        "nuevo@test.com",
                        "password123"
                );

        mockMvc.perform(post("/usuarios/admin/create")
                        .param("tipo", "CLIENTE")
                        .header("Authorization", "Bearer " + clienteToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUserByAdmin_ShouldReturnUnauthorized_WhenNoToken() throws Exception {

        UserRegistroDTO dto =
                new UserRegistroDTO(
                        "Nuevo",
                        "Usuario",
                        "44445555",
                        "nuevo@test.com",
                        "password123"
                );

        mockMvc.perform(post("/usuarios/admin/create")
                        .param("tipo", "CLIENTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
    */
}