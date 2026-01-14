package com.example.posapp.controller;


import com.example.posapp.repository.ProductRepository;
import com.example.posapp.repository.VentaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final VentaRepository ventaRepository;

    public HomeController(ProductRepository productRepository, VentaRepository ventaRepository) {
        this.productRepository = productRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {
        // Obtener datos reales
        Double ventasHoy = ventaRepository.sumTotalVentasHoy();
        long totalProductos = productRepository.count();
        long alertasStock = productRepository.countByStockLessThan(5); // Umbral de 5 unidades

        // Pasar a Thymeleaf (asegurando que ventas no sea null)
        model.addAttribute("ventasHoy", ventasHoy != null ? ventasHoy : 0.0);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("alertasStock", alertasStock);

        return "home";
    }
}
