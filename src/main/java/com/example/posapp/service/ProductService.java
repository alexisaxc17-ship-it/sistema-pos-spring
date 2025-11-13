package com.example.posapp.service;

import com.example.posapp.model.Product;
import com.example.posapp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> listarProductos() {
        return productRepository.findAll();
    }

    public void guardarProducto(Product product) {
        productRepository.save(product);
    }

    public Optional<Product> obtenerPorId(Long id) {
        return productRepository.findById(id);
    }

    public void eliminarProducto(Long id) {
        productRepository.deleteById(id);
    }

    public Optional<Product> buscarPorCodigo(String codigo) {
        return productRepository.findByCodigo(codigo);
    }
}
