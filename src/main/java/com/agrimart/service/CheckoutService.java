package com.agrimart.service;

import com.agrimart.entity.*;
import com.agrimart.repository.CartRepository;
import com.agrimart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    public Order checkout(User user) {

        List<Cart> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;
        for (Cart cart : cartItems) {
            total += cart.getProduct().getPrice() * cart.getQuantity();
        }

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .status("PLACED")
                .orderDate(LocalDateTime.now()) // ✅ FIXED
                .build();

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(user);

        return savedOrder;
    }
}
