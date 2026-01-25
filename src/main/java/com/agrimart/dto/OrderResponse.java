package com.agrimart.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {

    private Long id;
    private double totalAmount;
    private String status;
    private LocalDateTime orderDate;

    private String fullName;
    private String mobile;
    private String address;
    private String city;
    private String pincode;
    private String paymentMode;
}
