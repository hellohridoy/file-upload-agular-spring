package com.example.File_Image_upload.service;

import com.example.File_Image_upload.dto.DistrictDto;
import com.example.File_Image_upload.dto.DivisionDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DistrictService {
    // Get all districts
    List<DistrictDto> getAllDistricts();

    // Get districts by division code
    List<DistrictDto> getDistrictsByDivisionCode(String divisionCode);

    // Get district by code
    Optional<DistrictDto> getDistrictByCode(String code);

    // Get district by name
    Optional<DistrictDto> getDistrictByName(String name);

    @Transactional
    DistrictDto createDistrict(DistrictDto districtDto);

    @Transactional
    DistrictDto updateDistrict(String code, DistrictDto districtDto);

    // Delete district
    boolean deleteDistrict(String code);

    // Check if district exists
    boolean existsByCode(String code);

    // Search districts by name
    List<DistrictDto> searchDistrictsByName(String name);
}
