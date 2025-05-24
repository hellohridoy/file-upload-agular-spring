package com.example.File_Image_upload.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductCreateRequestDto {
    private String name;
    private String description;
    private BigDecimal price;
    private String sku;
    private Integer quantity;
    private String imageUrl;
    private Long categoryId;  // reference to Category ID
}



