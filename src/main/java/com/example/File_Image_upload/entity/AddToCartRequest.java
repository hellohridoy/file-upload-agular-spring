package com.example.File_Image_upload.entity;

import lombok.Data;

@Data
public class AddToCartRequest {
   private String userId;
    private Long productId;
    private Integer quantity = 1;
}
