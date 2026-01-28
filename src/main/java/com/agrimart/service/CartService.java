package com.agrimart.service;

import com.agrimart.dto.CartResponse;
import com.agrimart.entity.Cart;
import com.agrimart.entity.Product;
import com.agrimart.entity.User;
import com.agrimart.repository.CartRepository;
import com.agrimart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    // ✅ ADD TO CART
    public Cart addToCart(User user, Long productId, int quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Cart> existingCart =
                cartRepository.findByUserAndProduct(user, product);

        // ✅ If product already in cart → increase quantity
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        }

        // ✅ Else create new cart entry
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);

        return cartRepository.save(cart);
    }

    // ✅ VIEW CART (DTO – SAFE RESPONSE)
public List<CartResponse> viewCart(User user) {

    return cartRepository.findByUser(user)
            .stream()
            .map(cart -> new CartResponse(
                    cart.getId(),
                    cart.getProduct().getId(),
                    cart.getProduct().getName(),
                    cart.getProduct().getPrice(),
                    cart.getQuantity(),
                    cart.getProduct().getPrice() * cart.getQuantity()
            ))
            .toList();
}

    // ✅ REMOVE FROM CART
    public void removeFromCart(User user, Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cart.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        cartRepository.delete(cart);
    }

    // ✅ UPDATE CART ITEM QUANTITY
    // - Ownership check: User can only update their own cart items
    // - Validation: Quantity must be >= 1
    // - Returns updated CartResponse with new total price
    public CartResponse updateCartQuantity(User user, Long cartId, int quantity) {
        
        // Find cart item
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Ownership check: Ensure user owns this cart item
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Cannot modify other user's cart");
        }

        // Validation: Quantity must be at least 1
        if (quantity < 1) {
            throw new RuntimeException("Quantity must be at least 1");
        }

        // Update quantity and save
        cart.setQuantity(quantity);
        cart = cartRepository.save(cart);

        // Return updated item as CartResponse
        return new CartResponse(
                cart.getId(),
                cart.getProduct().getId(),
                cart.getProduct().getName(),
                cart.getProduct().getPrice(),
                cart.getQuantity(),
                cart.getProduct().getPrice() * cart.getQuantity()
        );
    }

    // ✅ CLEAR CART (used after checkout)
    public void clearCart(User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        cartRepository.deleteAll(cartItems);
    }
}
