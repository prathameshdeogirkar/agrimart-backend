package com.agrimart.service;

import com.agrimart.dto.CheckoutRequest;
import com.agrimart.entity.Cart;
import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import com.agrimart.repository.CartRepository;
import com.agrimart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Transactional
    public Order checkout(User user, CheckoutRequest request) {

        List<Cart> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = cartItems.stream()
                .mapToDouble(c -> c.getProduct().getPrice() * c.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .status("PLACED")
                .orderDate(LocalDateTime.now())

                // ✅ CHECKOUT DETAILS
                .fullName(request.getFullName())
                .mobile(request.getMobile())
                .address(request.getAddress())
                .city(request.getCity())
                .pincode(request.getPincode())
                .paymentMode(request.getPaymentMode())

                .build();

        Order savedOrder = orderRepository.save(order);

        // 🧹 clear cart after order
        cartService.clearCart(user);

        return savedOrder;
    }
}
