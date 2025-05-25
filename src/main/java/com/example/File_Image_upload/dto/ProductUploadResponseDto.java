package com.example.File_Image_upload.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductUploadResponseDto {
    private boolean success;
    private String message;
    private int totalRows;
    private int successfullyProcessed;
    private int failed;
    private List<ProductUploadError> errors;
    private List<String> warnings;

    public ProductUploadResponseDto() {}

    public ProductUploadResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
