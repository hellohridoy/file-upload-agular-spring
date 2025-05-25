package com.example.File_Image_upload.dto;

import lombok.Data;

@Data
public class ProductUploadError {
    private int rowNumber;
    private String field;
    private String message;
    private String value;

    public ProductUploadError() {}

    public ProductUploadError(int rowNumber, String field, String message, String value) {
        this.rowNumber = rowNumber;
        this.field = field;
        this.message = message;
        this.value = value;
    }
}
