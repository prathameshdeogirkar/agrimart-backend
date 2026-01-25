package com.agrimart.controller;

import com.agrimart.dto.CartResponse;
import com.agrimart.entity.Cart;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // ✅ ADD TO CART
    @PostMapping("/add")
    public Cart addToCart(
            @RequestParam Long productId,
            @RequestParam int quantity,
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartService.addToCart(user, productId, quantity);
    }

    // ✅ VIEW CART (FIXED)
@GetMapping
public List<CartResponse> viewCart(Authentication authentication) {

    String email = authentication.getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return cartService.viewCart(user);
}


    // ✅ REMOVE FROM CART
    @DeleteMapping("/{cartId}")
    public void removeFromCart(
            @PathVariable Long cartId,
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.removeFromCart(user, cartId);
    }
}
