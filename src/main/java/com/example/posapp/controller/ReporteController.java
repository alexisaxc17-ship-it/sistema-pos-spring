package com.example.posapp.controller;

import com.example.posapp.model.Venta;
import com.example.posapp.service.ReporteService;
import com.example.posapp.util.ReporteExcelGenerator;
import com.example.posapp.util.ReportePDFGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    // Mostrar formulario
    @GetMapping
    public String mostrarFormulario() {
        return "reportes/formulario"; // Tu template Thymeleaf
    }

    // Buscar ventas por rango de fechas
    @PostMapping("/buscar")
    public String buscarReporte(@RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                @RequestParam("fin")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
                                Model model) {

        // Obtener ventas mediante el servicio
        List<Venta> ventas = reporteService.obtenerVentasPorRango(inicio, fin);

        if (ventas == null) ventas = new ArrayList<>();

        // Eliminar elementos null (si los hubiera) e inicializar detalles vacíos
        ventas = ventas.stream()
                .filter(Objects::nonNull)
                .peek(v -> {
                    if (v.getDetalles() == null) v.setDetalles(new ArrayList<>());
                })
                .collect(Collectors.toList());

        // Calcular total general (si no hay ventas será 0)
        double totalGeneral = ventas.stream().mapToDouble(v -> v.getTotal()).sum();

        // Agregar atributos al modelo
        model.addAttribute("ventas", ventas);
        model.addAttribute("totalGeneral", totalGeneral);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);

        return "reportes/lista"; // Template donde se muestra la lista
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        List<Venta> ventas = reporteService.obtenerVentasPorRango(inicio, fin);
        double total = (ventas != null) ? ventas.stream().mapToDouble(Venta::getTotal).sum() : 0;

        try (ByteArrayInputStream pdf = ReportePDFGenerator.generarReportePDF(ventas, total)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ventas.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        List<Venta> ventas = reporteService.obtenerVentasPorRango(inicio, fin);
        double total = (ventas != null) ? ventas.stream().mapToDouble(Venta::getTotal).sum() : 0;

        try (ByteArrayInputStream excel = ReporteExcelGenerator.generarReporteExcel(ventas, total)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_ventas.xlsx")
                    // Tipo de contenido oficial para .xlsx
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

}
