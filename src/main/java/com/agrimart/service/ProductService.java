package com.agrimart.service;

import com.agrimart.entity.Product;
import com.agrimart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public Product update(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getPrice() > 0) {
            product.setPrice(productDetails.getPrice());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getImageUrl() != null) {
            product.setImageUrl(productDetails.getImageUrl());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getMrp() != null) {
            product.setMrp(productDetails.getMrp());
        }
        if (productDetails.getUnitSize() != null) {
            product.setUnitSize(productDetails.getUnitSize());
        }
        if (productDetails.getShelfLife() != null) {
            product.setShelfLife(productDetails.getShelfLife());
        }
        if (productDetails.getHealthBenefits() != null) {
            product.setHealthBenefits(productDetails.getHealthBenefits());
        }
        if (productDetails.getStorageAdvice() != null) {
            product.setStorageAdvice(productDetails.getStorageAdvice());
        }
        if (productDetails.getFarmerName() != null) {
            product.setFarmerName(productDetails.getFarmerName());
        }
        if (productDetails.getMarketedBy() != null) {
            product.setMarketedBy(productDetails.getMarketedBy());
        }
        if (productDetails.getManufacturerDetails() != null) {
            product.setManufacturerDetails(productDetails.getManufacturerDetails());
        }
        if (productDetails.getFssaiLicense() != null) {
            product.setFssaiLicense(productDetails.getFssaiLicense());
        }
        if (productDetails.getGalleryImages() != null) {
            product.setGalleryImages(productDetails.getGalleryImages());
        }
        // Support toggling active status
        product.setActive(productDetails.isActive());

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // Soft Delete: Set active to false instead of deleting row
        product.setActive(false);
        productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    // Admin view: All products (Active + Disabled)
    public org.springframework.data.domain.Page<Product> getAllAdmin(String search,
            org.springframework.data.domain.Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
        }
        return productRepository.findAll(pageable);
    }

    // Public view: Only active products
    public org.springframework.data.domain.Page<Product> getAllActive(String search,
            org.springframework.data.domain.Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(search.trim(), pageable);
        }
        return productRepository.findByActiveTrue(pageable);
    }

    public List<Product> getByCategory(String category) {
        return productRepository.findByCategoryAndActiveTrue(category);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

}
