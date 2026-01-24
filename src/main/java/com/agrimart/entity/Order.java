package com.agrimart.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private double totalAmount;

    private String status;

    private LocalDateTime orderDate;

    // ✅ ADD THESE
    private String fullName;
    private String mobile;
    private String address;
    private String city;
    private String pincode;
    private String paymentMode;
}
