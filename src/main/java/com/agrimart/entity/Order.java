package com.agrimart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User-facing Order ID (e.g., AGM-2026-123456)
    private String publicOrderId;

    @ManyToOne
    private User user;

    private double totalAmount;
    private String status;
    private LocalDateTime orderDate;

    private String fullName;
    private String mobile;
    private String address;
    private String city;
    private String pincode;
    private String paymentMode;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private java.util.List<OrderItem> orderItems;

    private String razorpayOrderId;
    private String razorpayPaymentId;
}
