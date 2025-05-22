package com.example.File_Image_upload.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpazilaDto {
    private long id;
    private String code;
    private String name;           // Upazila name
    private String districtCode;   // Parent district reference
    private String districtName;   // Parent district name for display
    private String divisionCode;   // Grand-parent division reference (optional but useful)
    private String divisionName;   // Grand-parent division name for breadcrumb
}
