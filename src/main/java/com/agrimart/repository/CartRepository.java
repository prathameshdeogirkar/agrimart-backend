package com.agrimart.repository;

import com.agrimart.entity.Cart;
import com.agrimart.entity.User;
import com.agrimart.entity.Product; // ✅ ADD THIS
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndProduct(User user, Product product);

    List<Cart> findByUser(User user);
}
