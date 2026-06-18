package com.microservicio.pagos;

import com.microservicio.pagos.aws.StorageService;
import com.microservicio.pagos.dto.ProcesarPagoRequestDTO;
import com.microservicio.pagos.dto.ProcesarPagoResponseDTO;
import com.microservicio.pagos.exception.ResourceNotFoundException;
import com.microservicio.pagos.service.PagoService;
import com.microservicio.pagos.service.PdfGeneratorService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("tests")
@Transactional
@Tag("aws")
public class PagosAWSTests {
    @Autowired
    private StorageService storageService;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Test
    void shouldUploadPdfToS3() throws Exception {
        byte[] content = "PDF TEST CONTENT".getBytes();
        String key = storageService.uploadBytes(
                content,
                "test.pdf",
                "application/pdf"
        );
        assertThat(key).isNotNull().contains(".pdf");
    }

    @Test
    void shouldUploadAndDownloadPdf() throws Exception {
        byte[] original = "PDF CONTENT".getBytes();
        String key = storageService.uploadBytes(
                original,
                "test.pdf",
                "application/pdf"
        );
        byte[] downloaded = storageService.getFile(key);
        assertThat(downloaded).isEqualTo(original);
    }

    @Test
    void shouldDeletePdf() throws Exception {
        byte[] original = "PDF CONTENT".getBytes();
        String key = storageService.uploadBytes(
                original,
                "delete-test.pdf",
                "application/pdf"
        );
        storageService.deleteFile(key);
        assertThatThrownBy(() -> storageService.getFile(key)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldCreateLocalCacheAfterDownload() throws Exception {
        byte[] original = "CACHE TEST".getBytes();
        String key = storageService.uploadBytes(
                original,
                "cache-test.pdf",
                "application/pdf"
        );
        Path cacheFile = Paths.get("/tmp/uploads/pdfs", key);
        Files.deleteIfExists(cacheFile);
        storageService.getFile(key);
        assertThat(Files.exists(cacheFile)).isTrue();
    }

    @Test
    void shouldGenerateAndStorePdf() throws Exception {
        byte[] pdf = pdfGeneratorService.generarComprobantePdf(
                "BOLETA",
                "B001-000001",
                5,
                150.0,
                null,
                null
        );
        String key = storageService.uploadBytes(
                pdf,
                "receipt.pdf",
                "application/pdf"
        );
        byte[] downloaded = storageService.getFile(key);
        assertThat(downloaded).isNotEmpty();
        assertThat(new String(downloaded, 0, 4)).isEqualTo("%PDF");
    }
}