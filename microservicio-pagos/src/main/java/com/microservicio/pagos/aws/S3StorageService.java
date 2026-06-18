package com.microservicio.pagos.aws;

import com.microservicio.pagos.exception.FileStorageException;
import com.microservicio.pagos.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class S3StorageService implements StorageService {
    @Value("${spring.destination.folder}")
    private String destinationFolder;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${s3.base-folder}")
    private String baseFolder;

    @Autowired
    private S3Client s3Client;

    private String buildKey(String key) {
        if (baseFolder.endsWith("/")) {
            return baseFolder + key;
        }
        return baseFolder + "/" + key;
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String key = UUID.randomUUID().toString() + extension;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(buildKey(key))
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
        return key;
    }

    @Override
    public String uploadBytes(byte[] content, String filename, String contentType) throws IOException {
        String key = UUID.randomUUID() + "-" + filename;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(buildKey(key))
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(content));
        return key;
    }

    @Override
    public byte[] getFile(String key) throws IOException {
        Path localFile = Paths.get(destinationFolder, key);
        if (Files.exists(localFile)) {
            return Files.readAllBytes(localFile);
        }
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(buildKey(key))
                .build();
        try {
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
            Path parent = localFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(localFile, response.asByteArray());
            return response.asByteArray();
        } catch (NoSuchKeyException e) {
            throw new ResourceNotFoundException("PDF no encontrado");
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            this.s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(buildKey(key))
                    .build());
        } catch (S3Exception e) {
            throw new FileStorageException("Failed to delete PDF file from S3: " + e.awsErrorDetails().errorMessage());
        }
        Path cachedFile = Paths.get(destinationFolder, key);
        try {
            Files.deleteIfExists(cachedFile);
        } catch (IOException e) {
            log.warn("Failed to delete cached PDF file" + e);
        }
    }
}
