package com.product.controller;


import com.product.entity.Product;
import com.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Service
public class ProductController {

    @Autowired
    private ProductRepository productRepository;


    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductbyID(@PathVariable Long id) {
        Product product = productRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found" + id));
        return ResponseEntity.ok(product);
    }
}

