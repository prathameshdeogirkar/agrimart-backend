package com.agrimart.controller;

import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.service.CheckoutService;
import com.agrimart.service.OrderService;
import com.agrimart.dto.CheckoutRequest;
import com.agrimart.dto.OrderResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CheckoutService checkoutService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    // ✅ CHECKOUT
    @PostMapping("/checkout")
    public Order checkout(
            Authentication authentication,
            @RequestBody CheckoutRequest request
    ) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return checkoutService.checkout(user, request);
    }

    // ✅ ORDER HISTORY
   @GetMapping
public List<OrderResponse> getMyOrders(Authentication authentication) {

    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return orderService.getOrdersForUser(user);
}

}
