package com.example.File_Image_upload.service;

import com.example.File_Image_upload.dto.DivisionDto;

import java.util.List;
import java.util.Optional;

public interface DivisionService {
    // Get all divisions
    List<DivisionDto> getAllDivisions(String searchParams);

    // Get division by code
    Optional<DivisionDto> getDivisionByCode(String code);

    // Get division by name
    Optional<DivisionDto> getDivisionByName(String name);

    // Create new division
    DivisionDto createDivision(DivisionDto divisionDto);

    // Update division
    DivisionDto updateDivision(String code, DivisionDto divisionDto);

    // Delete division
    boolean deleteDivision(String code);

    // Check if division exists
    boolean existsByCode(String code);

    // Search divisions by name
    List<DivisionDto> searchDivisionsByName(String name);

}
