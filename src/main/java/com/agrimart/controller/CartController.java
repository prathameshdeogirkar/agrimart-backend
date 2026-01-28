package com.agrimart.controller;

import com.agrimart.dto.CartResponse;
import com.agrimart.entity.Cart;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // 🔐 USER ONLY - Add to cart
    @PreAuthorize("hasRole('USER')")
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

    // 🔐 USER ONLY - View own cart
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public List<CartResponse> viewCart(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartService.viewCart(user);
    }

    // 🔐 USER ONLY - Remove from cart (user can only remove their own items)
    @PreAuthorize("hasRole('USER')")
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

    // 🔐 USER ONLY - Update cart item quantity (user can only update their own items)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{cartId}")
    public Object updateCartQuantity(
            @PathVariable Long cartId,
            @RequestParam int quantity,
            Authentication authentication
    ) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            return cartService.updateCartQuantity(user, cartId, quantity);
        } catch (RuntimeException e) {
            // Return error response for invalid quantity or unauthorized access
            return Map.of(
                    "success", false,
                    "error", e.getMessage()
            );
        }
    }
}
