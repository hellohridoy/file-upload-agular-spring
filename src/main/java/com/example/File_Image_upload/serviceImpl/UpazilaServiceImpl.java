package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.UpazilaDto;
import com.example.File_Image_upload.entity.District;
import com.example.File_Image_upload.entity.Upazila;
import com.example.File_Image_upload.repository.DistrictRepository;
import com.example.File_Image_upload.repository.UpazilaRepository;
import com.example.File_Image_upload.service.UpazilaService;
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
public class UpazilaServiceImpl implements UpazilaService {

    private final UpazilaRepository upazilaRepository;
    private final DistrictRepository districtRepository;

    @Override
    public List<UpazilaDto> getAllUpazilas() {
        log.info("Fetching all upazilas");

        List<Upazila> upazilas = upazilaRepository.findAllOrderByCode();

        return upazilas.stream()
            .map(upazila -> convertToDto(upazila))
            .collect(Collectors.toList());
    }

    @Override
    public List<UpazilaDto> getUpazilasByDistrictCode(String districtCode) {
        log.info("Fetching upazilas for district code: {}", districtCode);

        List<Upazila> upazilas = upazilaRepository.findByDistrictCodeWithDetails(districtCode);

        return upazilas.stream()
            .map(upazila -> convertToDto(upazila))
            .collect(Collectors.toList());
    }

    @Override
    public List<UpazilaDto> getUpazilasByDivisionCode(String divisionCode) {
        log.info("Fetching upazilas for division code: {}", divisionCode);

        List<Upazila> upazilas = upazilaRepository.findByDivisionCodeOrderByDistrictAndName(divisionCode);

        return upazilas.stream()
            .map(upazila -> convertToDto(upazila))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<UpazilaDto> getUpazilaByCode(String code) {
        log.info("Fetching upazila by code: {}", code);

        Optional<Upazila> upazila = upazilaRepository.findById(code);
        return upazila.map(this::convertToDto);
    }

    @Override
    public Optional<UpazilaDto> getUpazilaByName(String name) {
        log.info("Fetching upazila by name: {}", name);

        Optional<Upazila> upazila = upazilaRepository.findByNameIgnoreCase(name);
        if (upazila.isPresent()) {
            return Optional.of(convertToDto(upazila.get()));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public UpazilaDto createUpazila(UpazilaDto upazilaDto) {
        log.info("Creating new upazila: {}", upazilaDto.getName());

        // Validate input
        if (upazilaDto.getCode() == null || upazilaDto.getCode().trim().isEmpty()) {
            throw new RuntimeException("Upazila code is required");
        }

        if (upazilaDto.getName() == null || upazilaDto.getName().trim().isEmpty()) {
            throw new RuntimeException("Upazila name is required");
        }

        if (upazilaDto.getDistrictCode() == null || upazilaDto.getDistrictCode().trim().isEmpty()) {
            throw new RuntimeException("District code is required");
        }

        // Check if upazila already exists
        if (upazilaRepository.existsByCode(upazilaDto.getCode())) {
            throw new RuntimeException("Upazila with code " + upazilaDto.getCode() + " already exists");
        }

        // Verify district exists
        District district = districtRepository.findById(upazilaDto.getDistrictCode())
            .orElseThrow(() -> new RuntimeException("District not found with code: " + upazilaDto.getDistrictCode()));

        // Create upazila entity
        Upazila upazila = new Upazila();
        upazila.setCode(upazilaDto.getCode());
        upazila.setName(upazilaDto.getName());
        upazila.setDistrict(district);

        // Save upazila
        Upazila savedUpazila = upazilaRepository.save(upazila);

        log.info("Upazila created successfully with code: {}", savedUpazila.getCode());
        return convertToDto(savedUpazila);
    }

    @Override
    @Transactional
    public UpazilaDto updateUpazila(String code, UpazilaDto upazilaDto) {
        log.info("Updating upazila with code: {}", code);

        // Find existing upazila
        Upazila existingUpazila = upazilaRepository.findById(code)
            .orElseThrow(() -> new RuntimeException("Upazila not found with code: " + code));

        // Update fields
        if (upazilaDto.getName() != null && !upazilaDto.getName().trim().isEmpty()) {
            existingUpazila.setName(upazilaDto.getName());
        }

        // Update district if provided
        if (upazilaDto.getDistrictCode() != null && !upazilaDto.getDistrictCode().trim().isEmpty()) {
            District district = districtRepository.findById(upazilaDto.getDistrictCode())
                .orElseThrow(() -> new RuntimeException("District not found with code: " + upazilaDto.getDistrictCode()));
            existingUpazila.setDistrict(district);
        }

        // Save updated upazila
        Upazila updatedUpazila = upazilaRepository.save(existingUpazila);

        log.info("Upazila updated successfully: {}", updatedUpazila.getCode());
        return convertToDto(updatedUpazila);
    }

    @Override
    @Transactional
    public boolean deleteUpazila(String code) {
        log.info("Deleting upazila with code: {}", code);

        if (!upazilaRepository.existsByCode(code)) {
            log.warn("Upazila not found with code: {}", code);
            return false;
        }

        upazilaRepository.deleteById(code);
        log.info("Upazila deleted successfully: {}", code);
        return true;
    }

    @Override
    public boolean existsByCode(String code) {
        return upazilaRepository.existsByCode(code);
    }

    @Override
    public List<UpazilaDto> searchUpazilasByName(String name) {
        log.info("Searching upazilas by name containing: {}", name);

        List<Upazila> upazilas = upazilaRepository.findByNameContainingIgnoreCase(name);

        return upazilas.stream()
            .map(upazila -> convertToDto(upazila))
            .collect(Collectors.toList());
    }

    // Helper method to convert Entity to DTO
    private UpazilaDto convertToDto(Upazila upazila) {
        if (upazila == null) {
            return null;
        }

        UpazilaDto dto = new UpazilaDto();

        // Generate ID from code
        dto.setId(generateIdFromCode(upazila.getCode()));
        dto.setCode(upazila.getCode());
        dto.setName(upazila.getName());

        // Set district information
        if (upazila.getDistrict() != null) {
            dto.setDistrictCode(upazila.getDistrict().getCode());
            dto.setDistrictName(upazila.getDistrict().getName());

            // Set division information (for breadcrumb navigation)
            if (upazila.getDistrict().getDivision() != null) {
                dto.setDivisionCode(upazila.getDistrict().getDivision().getCode());
                dto.setDivisionName(upazila.getDistrict().getDivision().getName());
            }
        }

        return dto;
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
