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
}
