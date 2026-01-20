package com.agrimart.config;

import com.agrimart.entity.Product;
import com.agrimart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {

        if (productRepository.count() == 0) {

            productRepository.save(
                Product.builder()
                    .name("Apple")
                    .price(120)
                    .category("Fruits")
                    .build()
            );

            productRepository.save(
                Product.builder()
                    .name("Banana")
                    .price(60)
                    .category("Fruits")
                    .build()
            );

            productRepository.save(
                Product.builder()
                    .name("Tomato")
                    .price(40)
                    .category("Vegetables")
                    .build()
            );
        }
    }
}
