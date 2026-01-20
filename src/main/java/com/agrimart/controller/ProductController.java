package com.agrimart.controller;

import com.agrimart.entity.Product;
import com.agrimart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    // 🔐 ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    // 🌍 PUBLIC
    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return productService.getByCategory(category);
    }
}
