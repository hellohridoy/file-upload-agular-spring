package com.example.File_Image_upload.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer quantity;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
}
