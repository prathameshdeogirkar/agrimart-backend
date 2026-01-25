package com.agrimart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartResponse {

    private Long cartId;
    private Long productId;
    private String productName;
    private double price;
    private int quantity;
    private double totalPrice;
}
