package com.example.posapp.service;

import com.example.posapp.model.Product;
import com.example.posapp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> findAll() {
        return repo.findAll();
    }

    public Optional<Product> findById(Long id) {
        return repo.findById(id);
    }

    public Optional<Product> findByCodigoQr(String codigo) {
        return repo.findByCodigoQr(codigo);
    }

    @Transactional
    public Product save(Product product) {
        // si no tiene codigoQr, generar uno único
        if (product.getCodigoQr() == null || product.getCodigoQr().isBlank()) {
            product.setCodigoQr(UUID.randomUUID().toString());
        }
        return repo.save(product);
    }

    @Transactional
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
