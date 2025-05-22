package com.example.File_Image_upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictDto {
    private long id;
    private String code;
    private String name;
    private String divisionCode;
    private String divisionName;
}
