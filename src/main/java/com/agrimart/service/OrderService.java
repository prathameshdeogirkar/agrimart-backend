package com.agrimart.service;

import com.agrimart.dto.OrderResponse;
import com.agrimart.entity.Order;
import com.agrimart.entity.User;
import com.agrimart.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

        private final OrderRepository orderRepository;

        // ✅ ORDER HISTORY
        public List<OrderResponse> getOrdersForUser(User user) {

                return orderRepository.findByUserOrderByOrderDateDesc(user)
                                .stream()
                                .map(this::convertToResponse) // ✅ FIXED HERE
                                .toList();
        }

        // ✅ ADMIN: Get ALL orders
        public List<OrderResponse> getAllOrders() {
                return orderRepository.findAllByOrderByOrderDateDesc()
                                .stream()
                                .map(this::convertToResponse)
                                .toList();
        }

        // ✅ ADMIN: Update Order Status
        public OrderResponse updateOrderStatus(Long orderId, String status) {
                Order order = getOrderById(orderId);
                order.setStatus(status);
                Order savedOrder = orderRepository.save(order);
                return convertToResponse(savedOrder);
        }

        public Order getOrderById(Long id) {
                return orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        }

        // ✅ ENTITY → DTO
        public OrderResponse convertToResponse(Order order) {
                return OrderResponse.builder()
                                .orderId(order.getId())
                                .publicOrderId(order.getPublicOrderId())
                                .totalAmount(order.getTotalAmount())
                                .status(order.getStatus())
                                .orderDate(order.getOrderDate())
                                .fullName(order.getFullName())
                                .mobile(order.getMobile())
                                .address(order.getAddress())
                                .city(order.getCity())
                                .pincode(order.getPincode())
                                .paymentMode(order.getPaymentMode())
                                .items(order.getOrderItems() != null
                                                ? order.getOrderItems().stream()
                                                                .map(item -> com.agrimart.dto.OrderItemDto.builder()
                                                                                .productId(item.getProduct().getId())
                                                                                .productName(item.getProduct()
                                                                                                .getName())
                                                                                .productImageUrl(item.getProduct()
                                                                                                .getImageUrl())
                                                                                .quantity(item.getQuantity())
                                                                                .price(item.getPrice())
                                                                                .build())
                                                                .toList()
                                                : java.util.Collections.emptyList())
                                .build();
        }
}
