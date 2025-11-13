package com.example.posapp.util;

import com.example.posapp.model.Venta;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class ReportePDFGenerator {

    public static ByteArrayInputStream generarReportePDF(List<Venta> ventas, double totalGeneral) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Crear documento PDF
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título principal
            Paragraph titulo = new Paragraph("📋 Reporte de Ventas")
                    .setFont(PdfFontFactory.createFont())
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(titulo);

            // Crear tabla de 3 columnas
            Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 2}))
                    .setWidth(UnitValue.createPercentValue(100)); // ← ✅ Reemplazo correcto

            // Encabezados (corregido: aplicar estilo al Paragraph dentro de la Cell)
            Cell header1 = new Cell().add(new Paragraph("ID Venta").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            Cell header2 = new Cell().add(new Paragraph("Fecha").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            Cell header3 = new Cell().add(new Paragraph("Total ($)").setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);

            table.addHeaderCell(header1);
            table.addHeaderCell(header2);
            table.addHeaderCell(header3);



            // Filas de datos
            for (Venta venta : ventas) {
                table.addCell(String.valueOf(venta.getId()));
                table.addCell(String.valueOf(venta.getFecha()));
                table.addCell(String.format("%.2f", venta.getTotal()));
            }

            document.add(table);

            // Total general
            Paragraph total = new Paragraph("\nTotal General: $" + String.format("%.2f", totalGeneral))
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(10);
            document.add(total);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
