package com.agrimart.repository;

import com.agrimart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

        List<Product> findByCategory(String category);

        org.springframework.data.domain.Page<Product> findByNameContainingIgnoreCase(String name,
                        org.springframework.data.domain.Pageable pageable);

}
