package com.example.posapp.util;

import com.example.posapp.model.Venta;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ReporteExcelGenerator {

    public static ByteArrayInputStream generarReporteExcel(List<Venta> ventas, double totalGeneral) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte Ventas");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID Venta");
            header.createCell(1).setCellValue("Fecha");
            header.createCell(2).setCellValue("Total ($)");

            int fila = 1;
            for (Venta venta : ventas) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(venta.getId());
                row.createCell(1).setCellValue(venta.getFecha().toString());
                row.createCell(2).setCellValue(venta.getTotal());
            }

            Row totalRow = sheet.createRow(fila + 1);
            totalRow.createCell(1).setCellValue("Total General:");
            totalRow.createCell(2).setCellValue(totalGeneral);

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }
}
