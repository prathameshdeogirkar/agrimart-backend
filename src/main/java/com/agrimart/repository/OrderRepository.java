package com.agrimart.repository;

import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // ✅ Order history for user (latest first)
    List<Order> findByUserOrderByOrderDateDesc(User user);

    // ✅ Admin: Get all orders (latest first)
    List<Order> findAllByOrderByOrderDateDesc();
}
