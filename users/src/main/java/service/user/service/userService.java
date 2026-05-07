package service.user.service;

import org.springframework.stereotype.Service;
import service.user.dto.userRegistroDTO;
import service.user.dto.userResponseDTO;
import service.user.exception.resourceNotFoundException;
import service.user.model.tipoUser;
import service.user.model.user;
import service.user.repository.userRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class userService {

    private final userRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt

    // REGISTRO (solo clientes)
    public userResponseDTO registrar(userRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByDni(dto.dni())) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        user usuario = user.builder()
                .nombre(dto.nombre())
                .apellido(dto.apellido())
                .dni(dto.dni())
                .email(dto.email())
                .clave(passwordEncoder.encode(dto.clave()))
                .tipo(tipoUser.CLIENTE)
                .build();

        usuario = usuarioRepository.save(usuario);

        return toResponseDTO(usuario);
    }

    // MODIFICAR (solo admins pueden cambiar rol)
    public userResponseDTO actualizar(Integer id, userRegistroDTO dto, tipoUser nuevoTipo) {
        user usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new resourceNotFoundException("Usuario no encontrado"));

        // Solo se permite cambiar datos básicos + rol (rol solo por admin en BD o endpoint protegido)
        usuario.setNombre(dto.nombre());
        usuario.setApellido(dto.apellido());
        usuario.setDni(dto.dni());
        usuario.setEmail(dto.email());
        if (dto.clave() != null && !dto.clave().isBlank()) {
            usuario.setClave(passwordEncoder.encode(dto.clave()));
        }
        if (nuevoTipo != null) {
            usuario.setTipo(nuevoTipo);
        }

        usuario = usuarioRepository.save(usuario);
        return toResponseDTO(usuario);
    }

    // ELIMINAR
    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new resourceNotFoundException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    // BUSCAR TODOS (solo admins)
    public List<userResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCAR POR ID
    public userResponseDTO buscarPorId(Integer id) {
        user usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new resourceNotFoundException("Usuario no encontrado"));
        return toResponseDTO(usuario);
    }

    private userResponseDTO toResponseDTO(user u) {
        return new userResponseDTO(
                u.getIdUsuario(),
                u.getNombre(),
                u.getApellido(),
                u.getDni(),
                u.getEmail(),
                u.getTipo()
        );
    }
}