package com.agrimart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class OrderResponse {

    private Long orderId;
    private String publicOrderId;
    private double totalAmount;
    private String status;
    private LocalDateTime orderDate;

    private String fullName;
    private String mobile;
    private String address;
    private String city;
    private String pincode;
    private String paymentMode;

    private java.util.List<OrderItemDto> items;
}
