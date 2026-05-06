package service.user.controller;

import service.user.dto.userRegistroDTO;
import service.user.dto.userResponseDTO;
import service.user.model.tipoUser;
import service.user.service.userService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class userController {

    private final userService usuarioService;

    // POST - Registro público (cualquier persona se registra como cliente)
    @PostMapping("/registro")
    public ResponseEntity<userResponseDTO> registrar(@Valid @RequestBody userRegistroDTO dto) {
        userResponseDTO respuesta = usuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // PUT - Actualizar datos + rol (solo admins o endpoint protegido)
    @PutMapping("/{id}")
    public ResponseEntity<userResponseDTO> actualizar(
            @PathVariable Integer id,
            @RequestBody userRegistroDTO dto,
            @RequestParam(required = false) tipoUser tipo) { // opcional el tipo

        userResponseDTO actualizado = usuarioService.actualizar(id, dto, tipo);
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
    public ResponseEntity<List<userResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // GET - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<userResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
}