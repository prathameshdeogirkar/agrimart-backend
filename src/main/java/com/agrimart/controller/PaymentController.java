package com.agrimart.controller;

import com.agrimart.dto.OrderResponse;
import com.agrimart.dto.PaymentVerificationRequest;
import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.service.OrderService;
import com.agrimart.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder(Authentication authentication) {
        try {
            String email = authentication.getName();
            System.out.println("PaymentController: Received request from: " + email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> response = paymentService.createOrder(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(
            Authentication authentication,
            @RequestBody PaymentVerificationRequest request) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Order order = paymentService.verifyPayment(user, request);
            return ResponseEntity.ok(orderService.convertToResponse(order));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
