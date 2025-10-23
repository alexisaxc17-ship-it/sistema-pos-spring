package com.example.posapp.controller;

import com.example.posapp.model.Product;
import com.example.posapp.service.ProductService;
import com.example.posapp.util.QrUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/productos")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ✅ Listar productos
    @GetMapping
    public String listar(Model model, @ModelAttribute("mensaje") String mensaje) {
        model.addAttribute("productos", service.findAll());
        model.addAttribute("mensaje", mensaje);
        return "productos/list";
    }

    // ✅ Form nuevo producto
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Product());
        return "productos/form";
    }

    // ✅ Guardar (nuevo o editar)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Product producto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "productos/form";
        }

        try {
            boolean esNuevo = (producto.getId() == null);
            service.save(producto);

            if (esNuevo) {
                redirectAttrs.addFlashAttribute("mensaje", "✅ Producto agregado correctamente.");
            } else {
                redirectAttrs.addFlashAttribute("mensaje", "✏️ Producto actualizado correctamente.");
            }

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // ⚠️ Error por código duplicado u otra restricción
            redirectAttrs.addFlashAttribute("mensaje", "⚠️ El código de barras ingresado ya pertenece a otro producto.");
            return "redirect:/productos/nuevo";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("mensaje", "❌ Ocurrió un error al guardar el producto.");
            return "redirect:/productos/nuevo";
        }

        return "redirect:/productos";
    }


    // ✅ Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
        var op = service.findById(id);
        if (op.isEmpty()) {
            redirectAttrs.addFlashAttribute("mensaje", "❌ Producto no encontrado.");
            return "redirect:/productos";
        }
        model.addAttribute("producto", op.get());
        return "productos/form";
    }

    // ✅ Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        service.deleteById(id);
        redirectAttrs.addFlashAttribute("mensaje", "🗑️ Producto eliminado correctamente.");
        return "redirect:/productos";
    }

    // ✅ Mostrar QR como imagen PNG
    @GetMapping("/{id}/qr")
    public void qr(@PathVariable Long id, HttpServletResponse response) throws IOException {
        var op = service.findById(id);
        if (op.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        QrUtil.generateQrImageToResponse(op.get().getCodigoQr(), 250, 250, response);
    }
}
