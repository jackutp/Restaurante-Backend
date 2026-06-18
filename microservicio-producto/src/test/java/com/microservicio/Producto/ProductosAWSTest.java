package com.microservicio.Producto;

import com.microservicio.Producto.aws.StorageService;
import com.microservicio.Producto.exception.ResourceNotFoundException;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
public class ProductosAWSTest {
    @Autowired
    private StorageService storageService;
    @Value("${spring.destination.folder}")
    private String destinationFolder;

    private final List<String> uploadedKeys = new ArrayList<>();

    private MockMultipartFile buildRealImage() throws IOException {
        ClassPathResource resource = new ClassPathResource("uploads/Imagenes/imagen_test.jpg");
        return new MockMultipartFile(
                "imagen",
                "imagen_test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                resource.getInputStream()
        );
    }

    @AfterEach
    void cleanup() throws Exception {
        for (String key : uploadedKeys) {
            storageService.deleteFile(key);
        }
    }

    @Test
    void shouldUploadImageToS3() throws Exception {
        MockMultipartFile image = buildRealImage();
        String key = storageService.uploadFile(image);
        assertThat(key)
                .isNotNull()
                .endsWith(".jpg");
    }

    @Test
    void shouldUploadAndDownloadImage() throws Exception {
        MockMultipartFile image = buildRealImage();
        String key = storageService.uploadFile(image);
        byte[] downloaded = storageService.getFile(key);
        assertThat(downloaded)
                .isNotNull()
                .isNotEmpty();
        assertThat(downloaded.length).isEqualTo(image.getBytes().length);
    }

    @Test
    void shouldCreateLocalCacheAfterDownload() throws Exception {
        MockMultipartFile image = buildRealImage();
        String key = storageService.uploadFile(image);
        storageService.getFile(key);
        Path cacheFile = Paths.get(destinationFolder, key);
        assertThat(Files.exists(cacheFile)).isTrue();
    }

    @Test
    void shouldReadImageFromCacheWhenPresent() throws Exception {
        MockMultipartFile image = buildRealImage();
        String key = storageService.uploadFile(image);
        byte[] firstRead = storageService.getFile(key);
        Path cacheFile = Paths.get(destinationFolder, key);
        assertThat(Files.exists(cacheFile)).isTrue();
        byte[] secondRead = storageService.getFile(key);
        assertThat(secondRead).isEqualTo(firstRead);
    }

    @Test
    void shouldDeleteImage() throws Exception {
        MockMultipartFile image = buildRealImage();
        String key = storageService.uploadFile(image);
        storageService.getFile(key);
        storageService.deleteFile(key);
        assertThatThrownBy(() -> storageService.getFile(key)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldRejectInvalidContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "imagen",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello".getBytes()
        );
        assertThatThrownBy(() ->
                storageService.uploadFile(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Formato");
    }

    @Test
    void shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "imagen",
                "empty.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );
        assertThatThrownBy(() ->
                storageService.uploadFile(file))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldRejectLargeFile() {
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "imagen",
                "large.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                largeContent
        );
        assertThatThrownBy(() -> storageService.uploadFile(file))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("5MB");
    }

    @Test
    void shouldThrowWhenImageDoesNotExist() {
        assertThatThrownBy(() ->
                storageService.getFile(UUID.randomUUID() + ".jpg"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
