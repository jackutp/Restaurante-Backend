package service.user.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import service.user.dto.UserLoginRequestDTO;
import service.user.dto.UserRegistroDTO;
import service.user.dto.UserResponseDTO;
import service.user.model.TipoUser;
import service.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO request){
        return ResponseEntity.ok(usuarioService.login(request));
    }
    // POST - Registro público (cualquier persona se registra como cliente)
    @PostMapping("/registro")
    public ResponseEntity<UserResponseDTO> registrar(@Valid @RequestBody UserRegistroDTO dto) {
        UserResponseDTO respuesta = usuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // PUT - Actualizar datos + rol (solo admins o endpoint protegido)
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody UserRegistroDTO dto,
            @RequestParam(required = false) TipoUser tipo) { // opcional el tipo

        UserResponseDTO actualizado = usuarioService.actualizar(id, dto, tipo);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET - Listar todos (solo admins en producción)
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // GET - Buscar por ID
    @GetMapping("/{id}")
    //Este GET SOLO requiere de un administrador porque ESTAMOS HACIENDO PRUEBAS
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
}