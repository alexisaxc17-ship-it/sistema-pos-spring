package com.example.posapp.util;

import com.example.posapp.model.Venta;
import com.example.posapp.model.DetalleVenta;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.time.format.DateTimeFormatter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ReporteExcelGenerator {

    public static ByteArrayInputStream generarReporteExcel(List<Venta> ventas, double totalGeneral) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ventas");

            // Estilo de encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID Venta", "Fecha", "Producto", "Cantidad", "Subtotal"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Venta v : ventas) {
                for (DetalleVenta d : v.getDetalles()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(v.getId());

                    // Usa el formateador directamente aquí
                    String fechaLimpia = v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    row.createCell(1).setCellValue(fechaLimpia);

                    row.createCell(2).setCellValue(d.getProducto().getNombre());
                    row.createCell(3).setCellValue(d.getCantidad());
                    row.createCell(4).setCellValue(d.getSubtotal());
                }
            }

            // Fila de Total Final
            Row totalRow = sheet.createRow(rowIdx + 1);
            totalRow.createCell(3).setCellValue("TOTAL GENERAL:");
            totalRow.createCell(4).setCellValue(totalGeneral);

            for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }
}