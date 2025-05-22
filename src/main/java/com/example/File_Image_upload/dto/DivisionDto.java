package com.example.File_Image_upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class DivisionDto {
        private long id;
        private String code;           // Changed from id to code to match entity
        private String name;           // Fixed naming from divisionNam to name
        private String divisionCode;   // Keep this if you want separate display code
    }

