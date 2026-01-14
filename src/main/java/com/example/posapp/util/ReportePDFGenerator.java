package com.example.posapp.util;

import com.example.posapp.model.Venta;
import com.example.posapp.model.DetalleVenta;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import java.time.format.DateTimeFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class ReportePDFGenerator {

    public static ByteArrayInputStream generarReportePDF(List<Venta> ventas, double totalGeneral) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            document.add(new Paragraph("REPORTE DE VENTAS")
                    .setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Tabla con 5 columnas
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 4, 2, 2}));
            table.useAllAvailableWidth();

            // Encabezados
            table.addHeaderCell(new Cell().add(new Paragraph("#").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Fecha").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Producto").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Cant.").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Total ($)").setBold()));
            DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            int n = 1;
            for (Venta v : ventas) {
                for (DetalleVenta d : v.getDetalles()) {
                    table.addCell(String.valueOf(n++));

                    // CAMBIO: En lugar de v.getFecha().toString()
                    table.addCell(v.getFecha().format(formateador));

                    table.addCell(d.getProducto().getNombre());
                    table.addCell(String.valueOf(d.getCantidad()));
                    table.addCell(String.format("%.2f", d.getSubtotal()));
                }
            }

            document.add(table);

            // Fila de Total
            document.add(new Paragraph("\nTOTAL GENERAL: $" + String.format("%.2f", totalGeneral))
                    .setBold().setTextAlignment(TextAlignment.RIGHT));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}