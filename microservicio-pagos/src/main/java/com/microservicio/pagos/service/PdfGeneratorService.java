package com.microservicio.pagos.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.qrcode.ByteArray;
import com.microservicio.pagos.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfGeneratorService {
    public byte[] generarComprobantePdf(String tipo, String numeroCompleto,
                                        Integer mesaNumero, Double total,
                                        String ruc, String razonSocial) {
        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            // Título
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph(tipo.equals("BOLETA") ? "BOLETA ELECTRÓNICA" : "FACTURA ELECTRÓNICA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            // Información del restaurante
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            document.add(new Paragraph("LE BON GOUT", boldFont));
            document.add(new Paragraph("Av. Tacna y Arica 1234 - Arequipa, Perú"));
            document.add(new Paragraph("RUC: 20601234567"));
            document.add(new Paragraph("Teléfono: +51 963 168 458"));
            document.add(new Paragraph(" "));
            // Información del comprobante
            document.add(new Paragraph("Número: " + numeroCompleto, boldFont));
            document.add(new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
            document.add(new Paragraph("Mesa: " + mesaNumero));

            if (tipo.equals("FACTURA") && ruc != null && !ruc.isEmpty()) {
                document.add(new Paragraph("RUC: " + ruc));
                document.add(new Paragraph("Razón Social: " + razonSocial));
            }
            document.add(new Paragraph(" "));
            // Tabla de producto
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            PdfPCell cell1 = new PdfPCell(new Paragraph("Descripción", boldFont));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Total", boldFont));
            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell("Consumo en mesa");
            table.addCell("S/ " + String.format("%.2f", total));
            document.add(table);
            // Total
            document.add(new Paragraph(" "));
            Paragraph totalParagraph = new Paragraph("TOTAL A PAGAR: S/ " + String.format("%.2f", total), boldFont);
            totalParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalParagraph);
            // Mensaje de agradecimiento
            document.add(new Paragraph(" "));
            Paragraph gracias = new Paragraph("Gracias por su visita, que tenga buen día", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC));
            gracias.setAlignment(Element.ALIGN_CENTER);
            document.add(gracias);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new FileStorageException("NO se pudo guardar el recibo");
        }
    }
}