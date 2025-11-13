package com.example.posapp.service;

import com.example.posapp.model.DetalleVenta;
import com.example.posapp.model.Product;
import com.example.posapp.model.Venta;
import com.example.posapp.repository.DetalleVentaRepository;
import com.example.posapp.repository.ProductRepository;
import com.example.posapp.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductRepository productRepository;

    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        ProductRepository productRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Venta registrarVenta(List<DetalleVenta> detalles) {
        Venta venta = new Venta();
        double total = 0;

        // Guardar venta primero para generar ID
        ventaRepository.save(venta);

        for (DetalleVenta d : detalles) {
            Product producto = d.getProducto();
            if (producto != null) {
                double subtotal = d.getCantidad() * producto.getPrecio();
                d.setSubtotal(subtotal);
                total += subtotal;

                int nuevoStock = producto.getStock() - d.getCantidad();
                if (nuevoStock < 0) throw new RuntimeException("Stock insuficiente: " + producto.getNombre());
                producto.setStock(nuevoStock);
                productRepository.save(producto);
            }

            d.setVenta(venta);
        }

        venta.setTotal(total);
        detalleVentaRepository.saveAll(detalles);
        ventaRepository.save(venta);

        return venta;
    }

    public List<Venta> findByFechaBetween(LocalDate inicio, LocalDate fin) {
        OffsetDateTime inicioOffset = inicio.atStartOfDay().atOffset(ZoneOffset.ofHours(-6));
        OffsetDateTime finOffset = fin.atTime(LocalTime.MAX).atOffset(ZoneOffset.ofHours(-6));
        return ventaRepository.findByRangoFechas(inicioOffset, finOffset);
    }
}
