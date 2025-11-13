package com.example.posapp.controller;

import com.example.posapp.model.Product;
import com.example.posapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productService.listarProductos());
        return "productos/list";
    }

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        model.addAttribute("producto", new Product());
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Product producto,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "productos/form";
        }

        Optional<Product> existeCodigo = productService.buscarPorCodigo(producto.getCodigo());
        if (existeCodigo.isPresent() && !existeCodigo.get().getId().equals(producto.getId())) {
            model.addAttribute("codigoError", "El código de barra ya existe");
            return "productos/form";
        }

        productService.guardarProducto(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Product> productoOpt = productService.obtenerPorId(id);
        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
            return "productos/form";
        } else {
            model.addAttribute("mensaje", "Producto no encontrado");
            return "redirect:/productos";
        }
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        Optional<Product> productoOpt = productService.obtenerPorId(id);
        if (productoOpt.isPresent()) {
            productService.eliminarProducto(id);
        } else {
            model.addAttribute("mensaje", "Producto no encontrado");
        }
        return "redirect:/productos";
    }
}
