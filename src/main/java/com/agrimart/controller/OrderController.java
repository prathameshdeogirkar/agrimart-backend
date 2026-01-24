package com.agrimart.controller;

import com.agrimart.dto.CheckoutRequest;
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
    public Order checkout(
            Authentication authentication,
            @RequestBody CheckoutRequest checkoutRequest
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ FIXED CALL (THIS WAS THE BUG)
        return checkoutService.checkout(user, checkoutRequest);
    }
}
