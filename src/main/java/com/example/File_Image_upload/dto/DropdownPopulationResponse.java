package com.example.File_Image_upload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropdownPopulationResponse {

    @JsonProperty("postOffice")
    private PostOfficeDropdownDto postOffice;

    @JsonProperty("division")
    private DivisionDropdownDto division;

    @JsonProperty("district")
    private DistrictDropdownDto district;

    @JsonProperty("upazila")
    private UpazilaDropdownDto upazila;

    @JsonProperty("autoSetInstructions")
    private AutoSetInstructions autoSetInstructions;

    // Individual DTOs for each dropdown

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostOfficeDropdownDto {
        private long id;
        private String code;
        private String name;
        private String displayText; // For dropdown display
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DivisionDropdownDto {
        private long id;
        private String code;
        private String name;
        private String displayText;
        private boolean shouldAutoSelect;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DistrictDropdownDto {
        private long id;
        private String code;
        private String name;
        private String divisionCode;
        private String displayText;
        private boolean shouldAutoSelect;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpazilaDropdownDto {
        private long id;
        private String code;
        private String name;
        private String districtCode;
        private String displayText;
        private boolean shouldAutoSelect;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AutoSetInstructions {
        private String divisionToSelect;
        private String districtToSelect;
        private String upazilaToSelect;
        private String postOfficeToSelect;
        private String message;
    }
}
