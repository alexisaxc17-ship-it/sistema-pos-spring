package com.example.posapp.controller;

import com.example.posapp.model.DetalleVenta;
import com.example.posapp.model.Product;
import com.example.posapp.service.VentaService;
import com.example.posapp.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ventas")
@SessionAttributes("carrito")
public class VentaController {

    private final ProductRepository productRepository;
    private final VentaService ventaService;

    public VentaController(ProductRepository productRepository, VentaService ventaService) {
        this.productRepository = productRepository;
        this.ventaService = ventaService;
    }

    @ModelAttribute("carrito")
    public List<DetalleVenta> carrito() {
        return new ArrayList<>();
    }

    @GetMapping
    public String mostrarVentas(Model model, @ModelAttribute("carrito") List<DetalleVenta> carrito) {
        double total = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        model.addAttribute("total", total);
        return "ventas/venta";
    }

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam String codigo, @RequestParam int cantidad,
                                  @ModelAttribute("carrito") List<DetalleVenta> carrito) {

        Optional<Product> opt = productRepository.findByCodigo(codigo);

        if (opt.isPresent()) {
            Product producto = opt.get();

            // Verificar si el producto ya está en el carrito
            DetalleVenta detalleExistente = carrito.stream()
                    .filter(d -> d.getProducto().getId().equals(producto.getId()))
                    .findFirst().orElse(null);

            if (detalleExistente != null) {
                detalleExistente.setCantidad(detalleExistente.getCantidad() + cantidad);
                detalleExistente.setSubtotal(detalleExistente.getCantidad() * producto.getPrecio());
            } else {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setSubtotal(producto.getPrecio() * cantidad);
                carrito.add(detalle);
            }
        }

        return "redirect:/ventas";
    }

    @PostMapping("/finalizar")
    public String finalizarVenta(@ModelAttribute("carrito") List<DetalleVenta> carrito, HttpSession session) {
        if (!carrito.isEmpty()) {
            ventaService.registrarVenta(carrito);
            carrito.clear();
        }
        return "redirect:/ventas?success";
    }
}

