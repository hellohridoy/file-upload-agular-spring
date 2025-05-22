package com.example.File_Image_upload.entity;

import lombok.Data;

@Data
public class UpdateCartRequest {
    private Long cartItemId;
    private Integer quantity;
}
