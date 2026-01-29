package com.agrimart.controller;

import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import com.agrimart.repository.UserRepository;
import com.agrimart.service.CheckoutService;
import com.agrimart.service.OrderService;
import com.agrimart.dto.CheckoutRequest;
import com.agrimart.dto.OrderResponse;
import com.agrimart.dto.OrderStatusUpdateDTO;
import com.agrimart.service.InvoiceService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
        private final InvoiceService invoiceService;

        // 🔐 USER & ADMIN - Checkout (create order from cart)
        @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
        @PostMapping("/checkout")
        public OrderResponse checkout(
                        Authentication authentication,
                        @RequestBody CheckoutRequest request) {
                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Order order = checkoutService.checkout(user, request);

                return orderService.convertToResponse(order);
        }

        // 🔐 USER & ADMIN - Download Invoice
        @PreAuthorize("isAuthenticated()")
        @GetMapping("/{id}/invoice")
        public org.springframework.http.ResponseEntity<byte[]> getInvoice(
                        @PathVariable Long id,
                        Authentication authentication) throws java.io.IOException {

                Order order = orderService.getOrderById(id);

                // Security Check: User can only access their own orders unless ADMIN
                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                boolean isAdmin = authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
                        throw new RuntimeException(
                                        "Access Denied: You can only download invoices for your own orders.");
                }

                byte[] pdfBytes = invoiceService.generateInvoice(order);

                String filename = (order.getPublicOrderId() != null) ? order.getPublicOrderId() : String.valueOf(id);

                return org.springframework.http.ResponseEntity.ok()
                                .header("Content-Type", "application/pdf")
                                .header("Content-Disposition", "attachment; filename=invoice_" + filename + ".pdf")
                                .body(pdfBytes);
        }

        // 🔐 USER & ADMIN - Get own order history (users can only see their own orders)
        @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
        @GetMapping
        public List<OrderResponse> getMyOrders(Authentication authentication) {

                String email = authentication.getName();
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return orderService.getOrdersForUser(user);
        }

        // 🔐 ADMIN ONLY - View ALL Orders
        @PreAuthorize("hasRole('ADMIN')")
        @GetMapping("/all")
        public List<OrderResponse> getAllOrders() {
                return orderService.getAllOrders();
        }

        // 🔐 ADMIN ONLY - Update Order Status
        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping("/{id}/status")
        public OrderResponse updateStatus(
                        @PathVariable Long id,
                        @RequestBody OrderStatusUpdateDTO statusDto) {
                return orderService.updateOrderStatus(id, statusDto.getStatus());
        }
}
