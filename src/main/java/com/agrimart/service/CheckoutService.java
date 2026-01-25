package com.agrimart.service;

import com.agrimart.dto.CheckoutRequest;
import com.agrimart.entity.Cart;
import com.agrimart.entity.Order;
import com.agrimart.entity.OrderItem;
import com.agrimart.entity.User;
import com.agrimart.repository.CartRepository;
import com.agrimart.repository.OrderItemRepository;
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
    private final OrderItemRepository orderItemRepository;
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

                // Checkout details
                .fullName(request.getFullName())
                .mobile(request.getMobile())
                .address(request.getAddress())
                .city(request.getCity())
                .pincode(request.getPincode())
                .paymentMode(request.getPaymentMode())

                .build();

        // ✅ Save order
        Order savedOrder = orderRepository.save(order);

        // ✅ SAVE ORDER ITEMS
        for (Cart cart : cartItems) {
            OrderItem item = OrderItem.builder()
                    .order(savedOrder)
                    .product(cart.getProduct())
                    .quantity(cart.getQuantity())
                    .price(cart.getProduct().getPrice())
                    .build();

            orderItemRepository.save(item);
        }

        // 🧹 Clear cart AFTER saving items
        cartService.clearCart(user);

        return savedOrder;
    }
}
