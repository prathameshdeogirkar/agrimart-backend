package com.agrimart.service;

import com.agrimart.entity.Cart;
import com.agrimart.entity.Product;
import com.agrimart.entity.User;
import com.agrimart.repository.CartRepository;
import com.agrimart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional   // ✅ IMPORTANT: applies to ALL methods
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // ✅ ADD TO CART
    public Cart addToCart(User user, Long productId, int qty) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository
                .findByUserAndProduct(user, product)
                .orElse(
                        Cart.builder()
                                .user(user)
                                .product(product)
                                .quantity(0)
                                .build()
                );

        cart.setQuantity(cart.getQuantity() + qty);
        return cartRepository.save(cart);
    }

    // ✅ VIEW CART
    @Transactional(readOnly = true)
    public List<Cart> viewCart(User user) {
        return cartRepository.findByUser(user);
    }

    // ✅ REMOVE SINGLE ITEM FROM CART
    public void removeFromCart(User user, Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        cartRepository.delete(cart);
    }

    // ✅ CLEAR CART (USED IN CHECKOUT)
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }
}
