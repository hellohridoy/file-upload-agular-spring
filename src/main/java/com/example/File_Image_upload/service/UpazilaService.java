package com.example.File_Image_upload.service;

import com.example.File_Image_upload.dto.UpazilaDto;

import java.util.List;
import java.util.Optional;

public interface UpazilaService {
    // Get all upazilas
    List<UpazilaDto> getAllUpazilas();

    // Get upazilas by district code
    List<UpazilaDto> getUpazilasByDistrictCode(String districtCode);

    // Get upazilas by division code
    List<UpazilaDto> getUpazilasByDivisionCode(String divisionCode);

    // Get upazila by code
    Optional<UpazilaDto> getUpazilaByCode(String code);

    // Get upazila by name
    Optional<UpazilaDto> getUpazilaByName(String name);

    // Create new upazila
    UpazilaDto createUpazila(UpazilaDto upazilaDto);

    // Update upazila
    UpazilaDto updateUpazila(String code, UpazilaDto upazilaDto);

    // Delete upazila
    boolean deleteUpazila(String code);

    // Check if upazila exists
    boolean existsByCode(String code);

    // Search upazilas by name
    List<UpazilaDto> searchUpazilasByName(String name);
}
