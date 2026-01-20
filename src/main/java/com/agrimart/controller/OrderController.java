package com.agrimart.controller;

import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CheckoutService checkoutService;
    private final UserRepository userRepository;

    @PostMapping("/checkout")
    public Order checkout(Authentication authentication) {

        String email = authentication.getName(); // 🔥 FIX
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return checkoutService.checkout(user);
    }
}
