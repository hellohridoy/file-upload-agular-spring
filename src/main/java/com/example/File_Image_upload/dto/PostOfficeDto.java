package com.example.File_Image_upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostOfficeDto {
    private Long id;
    private String code;           // Post office code (8 digits)
    private String name;           // Post office name
    private String upazilaCode;    // Parent upazila reference
    private String upazilaName;    // Parent upazila name for display
    private String districtCode;   // Grand-parent district reference
    private String districtName;   // Grand-parent district name
    private String divisionCode;   // Great-grand-parent division reference
    private String divisionName;   // Great-grand-parent division name
}
