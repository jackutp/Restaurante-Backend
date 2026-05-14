package com.microservicio.Producto.Utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@Component
public class ImageUtils {
    private final String UPLOAD_DIR = "uploads/";
    private final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private final List<String> ALLOWED_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");
    public String guardarImagen(MultipartFile imagen) throws IOException {
        // Validar imagen
        validarImagen(imagen);
        // Crear directorio si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // Generar nombre único
        String nombreOriginal = imagen.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }
        String nombreUnico = UUID.randomUUID().toString() + extension;
        // Guardar archivo
        Path filePath = uploadPath.resolve(nombreUnico);
        Files.copy(imagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return UPLOAD_DIR + nombreUnico;
    }
    public byte[] obtenerImagen(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            }
            throw new RuntimeException("Imagen no encontrada");
        } catch (IOException e) {
            throw new RuntimeException("Error al leer la imagen: " + e.getMessage());
        }
    }
    public void eliminarImagen(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
    }
    private void validarImagen(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            throw new RuntimeException("El archivo de imagen está vacío");
        }
        if (imagen.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("La imagen excede el tamaño máximo de 5MB");
        }
        String contentType = imagen.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new RuntimeException("Formato no permitido. Use JPG o PNG");
        }
    }
}