package com.example.File_Image_upload.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    private Integer stock;

    private String sku;  // Stock Keeping Unit

    private Integer quantity; // Inventory stock count

    private String imageUrl;

    // Many products belong to one category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category;

    // Constructors, equals, hashCode can be generated or use Lombok @NoArgsConstructor/@AllArgsConstructor

    public Product() {}

    public Product(String name, String description, BigDecimal price, String sku, Integer quantity, String imageUrl, Categories category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.sku = sku;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}
