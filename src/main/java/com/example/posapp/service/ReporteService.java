package com.example.posapp.service;

import com.example.posapp.model.Venta;
import com.example.posapp.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private VentaRepository ventaRepository;

    public List<Venta> obtenerVentasPorRango(LocalDate inicio, LocalDate fin) {
        OffsetDateTime inicioOffset = inicio.atStartOfDay().atOffset(ZoneOffset.ofHours(-6));
        OffsetDateTime finOffset = fin.atTime(LocalTime.MAX).atOffset(ZoneOffset.ofHours(-6));

        return ventaRepository.findByRangoFechas(inicioOffset, finOffset);
    }

    public List<Venta> obtenerVentasPorFecha(LocalDate fecha) {
        return obtenerVentasPorRango(fecha, fecha);
    }
}
