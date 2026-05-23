package com.microservicio.Producto.Utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageStorageService implements  ImageStorageService{
    private final S3Client s3Client;
    @Value("${aws.s3.bucket}")
    private String bucketName;
    private final String CACHE_DIR = "uploads/";

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        String extention = ".";
        if(original != null || original.contains(".")){
            extention = original.substring(original.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID() + extention;
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return fileName;
    }

    @Override
    public byte[] getImage(String imagePath) {
        try {
            Path cachePath = Paths.get(CACHE_DIR, imagePath);

            if (Files.exists(cachePath)) {
                return Files.readAllBytes(cachePath);
            }

            // 2. Download from S3
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imagePath)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes =
                    s3Client.getObjectAsBytes(request);

            byte[] data = objectBytes.asByteArray();
            Files.createDirectories(Paths.get(CACHE_DIR));
            Files.write(
                    cachePath,
                    data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            return data;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving image", e);
        }
    }

    @Override
    public void deleteImage(String imagePath) {
        try {
            // delete from S3
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imagePath)
                    .build();
            s3Client.deleteObject(request);
            Path cachePath = Paths.get(CACHE_DIR, imagePath);
            Files.deleteIfExists(cachePath);

        } catch (Exception e) {
            throw new RuntimeException("Error deleting image", e);
        }
    }
}
