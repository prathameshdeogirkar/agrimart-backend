package com.agrimart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderItemDto {
    private Long productId;
    private String productName;
    private String productImageUrl;
    private int quantity;
    private double price;
}
