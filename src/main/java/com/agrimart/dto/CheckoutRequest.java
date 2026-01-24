package com.agrimart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {
    private String fullName;
    private String mobile;
    private String address;
    private String city;
    private String pincode;
    private String paymentMode; // COD
}
