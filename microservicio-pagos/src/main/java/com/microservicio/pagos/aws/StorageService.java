package com.microservicio.pagos.aws;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String uploadFile(MultipartFile file) throws IOException;

    String uploadBytes(byte[] content, String filename, String contenType) throws IOException;

    byte[] getFile(String key) throws IOException;

    void deleteFile(String key);
}
