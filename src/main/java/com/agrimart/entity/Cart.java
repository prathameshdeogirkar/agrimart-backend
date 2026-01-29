package com.agrimart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carts", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "product_id" })
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    private int quantity;
}
