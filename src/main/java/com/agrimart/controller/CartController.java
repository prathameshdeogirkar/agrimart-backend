package com.agrimart.controller;

import com.agrimart.entity.Cart;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    // ✅ ADD TO CART
    @PostMapping("/add")
    public Cart addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        User user = getLoggedInUser();
        return cartService.addToCart(user, productId, quantity);
    }

    // ✅ REMOVE FROM CART
    @DeleteMapping("/remove/{productId}")
    public void removeFromCart(@PathVariable Long productId) {
        User user = getLoggedInUser();
        cartService.removeFromCart(user, productId);
    }

    // ✅ VIEW CART
    @GetMapping
    public List<Cart> viewCart() {
        User user = getLoggedInUser();
        return cartService.viewCart(user);
    }

    // 🔐 Helper method
    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
