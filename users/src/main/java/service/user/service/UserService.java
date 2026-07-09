package service.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.annotation.Transactional;
import service.user.exception.ConflictException;
import service.user.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import service.user.dto.UserLoginRequestDTO;
import service.user.dto.UserRegistroDTO;
import service.user.dto.UserResponseDTO;
import service.user.exception.resourceNotFoundException;
import service.user.model.User;
import service.user.model.TipoUser;
import service.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public UserResponseDTO login(UserLoginRequestDTO request) {
        log.info("Intento de inicio de sesión para {}", request.email());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails user = usuarioRepository.findByEmail(request.email()).orElseThrow();
        log.info("Inicio de sesión exitoso para {}", request.email());
        return toResponseDTO((User) user);
    }

    // REGISTRO (solo clientes)
    @Transactional
    public UserResponseDTO registrar(UserRegistroDTO dto) {
        log.info("Registrando nuevo cliente {}", dto.email());
        if (usuarioRepository.existsByEmail(dto.email())) {
            log.warn("Intento de registro con email ya existente {}", dto.email());
            throw new ConflictException("El email ya está registrado");
        }
        if (usuarioRepository.existsByDni(dto.dni())) {
            log.warn("Intento de registro con DNI ya existente {}", dto.dni());
            throw new ConflictException("El DNI ya está registrado");
        }

        User usuario = User.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .dni(dto.dni())
                .email(dto.email())
                .clave(passwordEncoder.encode(dto.clave()))
                .tipo(TipoUser.CLIENTE)
                .build();

        usuario = usuarioRepository.save(usuario);
        log.info("Cliente {} registrado correctamente", usuario.getEmail());
        return toResponseDTO(usuario);
    }

    // MODIFICAR (solo admins pueden cambiar rol)
    @Transactional
    public UserResponseDTO actualizar(Integer id, UserRegistroDTO dto, TipoUser nuevoTipo) {
        log.info("Actualizando usuario {}", id);
        User usuario = usuarioRepository.findById(id).orElseThrow(() -> new resourceNotFoundException("Usuario no encontrado"));

        // Solo se permite cambiar datos básicos + rol (rol solo por admin en BD o endpoint protegido)
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setDni(dto.dni());
        usuario.setEmail(dto.email());
        if (dto.clave() != null && !dto.clave().isBlank()) {
            usuario.setClave(passwordEncoder.encode(dto.clave()));
        }
        if (nuevoTipo != null) {
            log.info("Rol del usuario {} cambiado a {}", id, nuevoTipo);
            usuario.setTipo(nuevoTipo);
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Usuario {} actualizado correctamente", usuario.getEmail());
        return toResponseDTO(usuario);
    }

    // ELIMINAR
    @Transactional
    public void eliminar(Integer id) {
        log.info("Eliminando usuario {}", id);
        if (!usuarioRepository.existsById(id)) {
            log.warn("Intento de eliminar usuario inexistente {}", id);
            throw new resourceNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario {} eliminado correctamente", id);
    }

    // BUSCAR TODOS (solo admins)
    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCAR POR ID
    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorId(Integer id) {
        User usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new resourceNotFoundException("Usuario no encontrado"));
        return toResponseDTO(usuario);
    }

    @Transactional
    public UserResponseDTO createUserByAdmin(UserRegistroDTO dto, TipoUser tipo) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByDni(dto.dni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }
        User usuario = User.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .dni(dto.dni())
                .email(dto.email())
                .clave(passwordEncoder.encode(dto.clave()))
                .tipo(tipo)
                .build();

        usuario = usuarioRepository.save(usuario);
        return toResponseDTO(usuario);
    }

    private UserResponseDTO toResponseDTO(User u) {
        return new UserResponseDTO(
                u.getIdUsuario(),
                u.getNombre(),
                u.getApellido(),
                u.getDni(),
                u.getEmail(),
                u.getTipo(),
                jwtService.getToken(u)
        );
    }

}