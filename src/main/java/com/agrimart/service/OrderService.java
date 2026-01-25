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

    public List<OrderResponse> getOrdersForUser(User user) {

        return orderRepository.findByUserOrderByOrderDateDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .fullName(order.getFullName())
                .mobile(order.getMobile())
                .address(order.getAddress())
                .city(order.getCity())
                .pincode(order.getPincode())
                .paymentMode(order.getPaymentMode())
                .build();
    }
}
