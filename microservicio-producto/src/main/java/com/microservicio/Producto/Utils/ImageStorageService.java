package com.microservicio.Producto.Utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    String saveImage(MultipartFile file) throws IOException;
    byte[] getImage(String imagePath);
    void deleteImage(String imagePath);
}
