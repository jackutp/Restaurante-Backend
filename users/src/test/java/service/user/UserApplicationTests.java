package service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import service.user.dto.UserLoginRequestDTO;
import service.user.dto.UserRegistroDTO;
import service.user.dto.UserResponseDTO;
import service.user.exception.ConflictException;
import service.user.exception.resourceNotFoundException;
import service.user.jwt.JwtService;
import service.user.model.TipoUser;
import service.user.model.User;
import service.user.repository.UserRepository;
import service.user.service.UserService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
class UserApplicationTests {
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;

	private UserRegistroDTO buildRegistroDTO(){
		return new UserRegistroDTO(
			"Kenji",
				"Perez",
				"44411122",
				"kenji@test.com",
				"password123"
		);
	}
	private UserLoginRequestDTO buildLoginDTO(){
		return new UserLoginRequestDTO("kenji@test.com", "password123");
	}

	@Test
	void contextLoads() {

	}

	@Test
	void shouldRegisterUser() {
		UserRegistroDTO dto = buildRegistroDTO();
		UserResponseDTO saved = userService.registrar(dto);
		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.idUsuario()).isNotNull();
		Assertions.assertThat(saved.nombre()).isEqualTo("Kenji");
		Assertions.assertThat(saved.apellido()).isEqualTo("Perez");
		Assertions.assertThat(saved.email()).isEqualTo("kenji@test.com");
		Assertions.assertThat(saved.tipo()).isEqualTo(TipoUser.CLIENTE);
		Assertions.assertThat(saved.token()).isNotBlank();

		User user = userRepository.findByEmail(dto.email()).orElseThrow();

		Assertions.assertThat(passwordEncoder.matches("password123", user.getClave())).isTrue();
	}

	@Test
	void shouldLoginUser() {
		UserRegistroDTO registroDTO = buildRegistroDTO();
		userService.registrar(registroDTO);
		UserLoginRequestDTO loginDTO = buildLoginDTO();
		UserResponseDTO response = userService.login(loginDTO);
		Assertions.assertThat(response).isNotNull();
		Assertions.assertThat(response.email()).isEqualTo("kenji@test.com");
		Assertions.assertThat(response.token()).isNotBlank();
	}

	@Test
	void shouldFindUserById() {
		UserRegistroDTO dto = buildRegistroDTO();
		UserResponseDTO saved = userService.registrar(dto);
		UserResponseDTO found = userService.buscarPorId(saved.idUsuario());
		Assertions.assertThat(found).isNotNull();
		Assertions.assertThat(found.idUsuario()).isEqualTo(saved.idUsuario());
		Assertions.assertThat(found.email()).isEqualTo(saved.email());
	}

	@Test
	void shouldListAllUsers() {
		userService.registrar(buildRegistroDTO());
		List<UserResponseDTO> result = userService.listarTodos();
		Assertions.assertThat(result).isNotEmpty();
	}

	@Test
	void shouldUpdateUser() {
		UserRegistroDTO dto = buildRegistroDTO();
		UserResponseDTO saved = userService.registrar(dto);
		UserRegistroDTO updatedDTO = new UserRegistroDTO(
				"Mario",
				"Gomez",
				"66611122",
				"mario@test.com",
				"newpassword123"
		);
		UserResponseDTO updated = userService.actualizar(
				saved.idUsuario(),
				updatedDTO,
				TipoUser.ADMINISTRADOR
		);

		Assertions.assertThat(updated.nombre()).isEqualTo("Mario");
		Assertions.assertThat(updated.apellido()).isEqualTo("Gomez");
		Assertions.assertThat(updated.email()).isEqualTo("mario@test.com");
		Assertions.assertThat(updated.tipo()).isEqualTo(TipoUser.ADMINISTRADOR);
		User user = userRepository.findById(saved.idUsuario()).orElseThrow();
		Assertions.assertThat(passwordEncoder.matches("newpassword123", user.getClave())).isTrue();
	}

	@Test
	void shouldDeleteUser() {
		UserRegistroDTO dto = buildRegistroDTO();
		UserResponseDTO saved = userService.registrar(dto);
		userService.eliminar(saved.idUsuario());
		Optional<User> found = userRepository.findById(saved.idUsuario());
		Assertions.assertThat(found).isEmpty();
	}

	@Test
	void shouldThrowWhenEmailAlreadyExists() {
		UserRegistroDTO dto = buildRegistroDTO();
		userService.registrar(dto);
		Assertions.assertThatThrownBy(() -> userService.registrar(dto))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("email ya está registrado");
	}

	@Test
	void shouldThrowWhenDniAlreadyExists() {
		UserRegistroDTO dto1 = buildRegistroDTO();
		UserRegistroDTO dto2 = new UserRegistroDTO(
				"Maria",
				"Lopez",
				"44411122",
				"maria@test.com",
				"password123"
		);
		userService.registrar(dto1);
		Assertions.assertThatThrownBy(() -> userService.registrar(dto2))
				.isInstanceOf(ConflictException.class)
				.hasMessageContaining("DNI ya está registrado");
	}

	@Test
	void shouldThrowWhenUserNotFoundById() {
		Assertions.assertThatThrownBy(() -> userService.buscarPorId(99999))
				.isInstanceOf(resourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenUpdatingNonExistingUser() {
		UserRegistroDTO dto = buildRegistroDTO();
		Assertions.assertThatThrownBy(() -> userService.actualizar(99999, dto, TipoUser.ADMINISTRADOR))
				.isInstanceOf(resourceNotFoundException.class);
	}

	@Test
	void shouldThrowWhenDeletingNonExistingUser() {
		Assertions.assertThatThrownBy(() -> userService.eliminar(99999))
				.isInstanceOf(resourceNotFoundException.class);
	}

	@Test
	void shouldGenerateValidJwtToken() {
		UserRegistroDTO dto = buildRegistroDTO();
		UserResponseDTO response = userService.registrar(dto);
		Assertions.assertThat(response.token()).isNotBlank();
		User user = userRepository.findByEmail(dto.email()).orElseThrow();
		String generatedToken = jwtService.getToken(user);
		Assertions.assertThat(generatedToken).isNotBlank();
		Assertions.assertThat(generatedToken.split("\\.")).hasSize(3);
	}

	@Test
	void shouldAuthenticateWithAuthenticationManager() {
		UserRegistroDTO dto = buildRegistroDTO();
		userService.registrar(dto);
		Authentication authentication = authenticationManager
				.authenticate( new UsernamePasswordAuthenticationToken(dto.email(), dto.clave()));
		Assertions.assertThat(authentication.isAuthenticated()).isTrue();
	}

	@Test
	void shouldFailLoginWithWrongPassword() {
		UserRegistroDTO dto = buildRegistroDTO();
		userService.registrar(dto);
		UserLoginRequestDTO badLogin = new UserLoginRequestDTO(
				"juan@test.com",
				"wrongpassword"
		);

		Assertions.assertThatThrownBy(() -> userService.login(badLogin))
				.isInstanceOf(AuthenticationException.class);
	}

	@Test
	void shouldUpdateUserWithoutChangingPassword() {
		UserRegistroDTO dto = buildRegistroDTO();
		UserResponseDTO saved = userService.registrar(dto);
		User existing = userRepository.findById(saved.idUsuario()).orElseThrow();
		String oldPassword = existing.getClave();
		UserRegistroDTO updatedDTO = new UserRegistroDTO(
				"Kenji Updated",
				"Perez Updated",
				"33388822",
				"updated@test.com",
				""
		);
		userService.actualizar(
				saved.idUsuario(),
				updatedDTO,
				null
		);
		User updated = userRepository.findById(saved.idUsuario()).orElseThrow();
		Assertions.assertThat(updated.getClave()).isEqualTo(oldPassword);
	}
}
