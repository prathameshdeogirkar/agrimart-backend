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

    @GetMapping
    public org.springframework.data.domain.Page<Product> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        org.springframework.data.domain.Pageable pageable = createPageable(page, size, sort);
        return productService.getAllActive(search, pageable);
    }

    // 🔐 ADMIN ONLY - View all products (including disabled)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public org.springframework.data.domain.Page<Product> getAllAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        org.springframework.data.domain.Pageable pageable = createPageable(page, size, sort);
        return productService.getAllAdmin(search, pageable);
    }

    private org.springframework.data.domain.Pageable createPageable(int page, int size, String[] sort) {
        String sortField = sort[0];
        String sortDirection = (sort.length > 1) ? sort[1] : "desc";

        org.springframework.data.domain.Sort sorting = sortDirection.equalsIgnoreCase("asc")
                ? org.springframework.data.domain.Sort.by(sortField).ascending()
                : org.springframework.data.domain.Sort.by(sortField).descending();

        return org.springframework.data.domain.PageRequest.of(page, size, sorting);
    }

    // ✅ PUBLIC - View product by ID
    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    // ✅ PUBLIC - Get products by category
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return productService.getByCategory(category);
    }

    // 🔐 ADMIN ONLY - Create product
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    // 🔐 ADMIN ONLY - Update product
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return productService.update(id, product);
    }

    // 🔐 ADMIN ONLY - Delete product
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

}
