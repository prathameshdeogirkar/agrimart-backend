package com.agrimart.repository;

import com.agrimart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

        List<Product> findByCategoryAndActiveTrue(String category);

        org.springframework.data.domain.Page<Product> findByActiveTrue(
                        org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name,
                        org.springframework.data.domain.Pageable pageable);

        org.springframework.data.domain.Page<Product> findByNameContainingIgnoreCase(String name,
                        org.springframework.data.domain.Pageable pageable);

}
