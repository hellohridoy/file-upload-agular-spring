// 1. DistrictDto
package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.DistrictDto;
import com.example.File_Image_upload.entity.District;
import com.example.File_Image_upload.entity.Division;
import com.example.File_Image_upload.repository.DistrictRepository;
import com.example.File_Image_upload.repository.DivisionRepository;
import com.example.File_Image_upload.service.DistrictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final DivisionRepository divisionRepository;

    @Override
    public List<DistrictDto> getAllDistricts() {
        log.info("Fetching all districts");

        List<District> districts = districtRepository.findAllOrderByCode();

        return districts.stream()
            .map(district -> convertToDto(district))
            .collect(Collectors.toList());
    }

    @Override
    public List<DistrictDto> getDistrictsByDivisionCode(String divisionCode) {
        log.info("Fetching districts for division code: {}", divisionCode);

        List<District> districts = districtRepository.findByDivisionCodeWithDivision(divisionCode);

        return districts.stream()
            .map(district -> convertToDto(district))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<DistrictDto> getDistrictByCode(String code) {
        log.info("Fetching district by code: {}", code);

        Optional<District> district = districtRepository.findById(code);
        return district.map(this::convertToDto);
    }

    @Override
    public Optional<DistrictDto> getDistrictByName(String name) {
        log.info("Fetching district by name: {}", name);

        Optional<District> district = districtRepository.findByNameIgnoreCase(name);
        if (district.isPresent()) {
            return Optional.of(convertToDto(district.get()));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public DistrictDto createDistrict(DistrictDto districtDto) {
        log.info("Creating new district: {}", districtDto.getName());

        if (districtRepository.existsByCode(districtDto.getCode())) {
            throw new RuntimeException("District with code " + districtDto.getCode() + " already exists");
        }

        District district = convertToEntity(districtDto);
        District savedDistrict = districtRepository.save(district);

        log.info("District created successfully with code: {}", savedDistrict.getCode());
        return convertToDto(savedDistrict);
    }

    @Override
    @Transactional
    public DistrictDto updateDistrict(String code, DistrictDto districtDto) {
        log.info("Updating district with code: {}", code);

        District existingDistrict = districtRepository.findById(code)
            .orElseThrow(() -> new RuntimeException("District not found with code: " + code));

        existingDistrict.setName(districtDto.getName());

        District updatedDistrict = districtRepository.save(existingDistrict);

        log.info("District updated successfully: {}", updatedDistrict.getCode());
        return convertToDto(updatedDistrict);
    }

    @Override
    @Transactional
    public boolean deleteDistrict(String code) {
        log.info("Deleting district with code: {}", code);

        if (!districtRepository.existsByCode(code)) {
            log.warn("District not found with code: {}", code);
            return false;
        }

        districtRepository.deleteById(code);
        log.info("District deleted successfully: {}", code);
        return true;
    }

    @Override
    public boolean existsByCode(String code) {
        return districtRepository.existsByCode(code);
    }

    @Override
    public List<DistrictDto> searchDistrictsByName(String name) {
        log.info("Searching districts by name containing: {}", name);

        List<District> districts = districtRepository.findByNameContainingIgnoreCase(name);

        return districts.stream()
            .map(district -> convertToDto(district))
            .collect(Collectors.toList());
    }

    // Helper method to convert Entity to DTO
    private DistrictDto convertToDto(District district) {
        if (district == null) {
            return null;
        }

        DistrictDto dto = new DistrictDto();

        // Generate ID from code
        dto.setId(generateIdFromCode(district.getCode()));
        dto.setCode(district.getCode());
        dto.setName(district.getName());

        // Set division information
        if (district.getDivision() != null) {
            dto.setDivisionCode(district.getDivision().getCode());
            dto.setDivisionName(district.getDivision().getName());
        }

        return dto;
    }

    // Helper method to convert DTO to Entity
    private District convertToEntity(DistrictDto districtDto) {
        District district = new District();
        district.setCode(districtDto.getCode());
        district.setName(districtDto.getName());

        // Set division
        if (districtDto.getDivisionCode() != null) {
            Division division = divisionRepository.findById(districtDto.getDivisionCode())
                .orElseThrow(() -> new RuntimeException("Division not found with code: " + districtDto.getDivisionCode()));
            district.setDivision(division);
        }

        return district;
    }

    // Helper method to generate consistent long ID from string code
    private long generateIdFromCode(String code) {
        if (code == null || code.isEmpty()) {
            return 0L;
        }

        try {
            return Long.parseLong(code);
        } catch (NumberFormatException e) {
            return Math.abs(code.hashCode());
        }
    }
}

