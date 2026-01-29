package com.agrimart.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String category;
    private String imageUrl;

    @Column(length = 1000)
    private String description;

    private Double mrp;
    private String unitSize;
    private String shelfLife;

    @Column(length = 1000)
    private String healthBenefits;

    private String storageAdvice;
    private String farmerName;
    private String marketedBy;
    private String manufacturerDetails;
    private String fssaiLicense;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private java.util.List<String> galleryImages = new java.util.ArrayList<>();
}
