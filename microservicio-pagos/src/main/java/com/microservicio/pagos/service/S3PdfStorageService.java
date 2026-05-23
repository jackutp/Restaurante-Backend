package com.microservicio.pagos.service;

import com.microservicio.pagos.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@RequiredArgsConstructor
public class S3PdfStorageService implements PdfStorageService{
    private final S3Client s3Client;
    @Value("${aws.s3.bucket}")
    private String bucket;
    private final String CACHE_DIR = "pdfs/";
    @Override
    public String savePdf(byte[] pdfBytes, String fileName) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(pdfBytes)
            );
            // optional cache
            Files.createDirectories(Paths.get(CACHE_DIR));
            Path cachePath = Paths.get(CACHE_DIR, fileName);
            Files.write(
                    cachePath,
                    pdfBytes,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            return fileName;

        } catch (Exception e) {
            throw new FileStorageException("Error saving PDF");
        }
    }

    @Override
    public byte[] getPdf(String fileName) {

        try {

            Path cachePath = Paths.get(CACHE_DIR, fileName);

            if (Files.exists(cachePath)) {
                return Files.readAllBytes(cachePath);
            }
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();
            ResponseBytes<GetObjectResponse> object =
                    s3Client.getObjectAsBytes(request);
            byte[] data = object.asByteArray();

            Files.createDirectories(Paths.get(CACHE_DIR));

            Files.write(
                    cachePath,
                    data,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            return data;

        } catch (Exception e) {
            throw new FileStorageException("Error retrieving PDF");
        }
    }

    @Override
    public void deletePdf(String fileName) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(request);

            Files.deleteIfExists(
                    Paths.get(CACHE_DIR, fileName)
            );

        } catch (Exception e) {
            throw new FileStorageException("Error deleting PDF");
        }
    }
}
