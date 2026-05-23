package com.microservicio.pagos.service;

public interface PdfStorageService {
    String savePdf(byte[] pdfBytes, String fileName);
    byte[] getPdf(String filename);
    void deletePdf(String filename);
}
