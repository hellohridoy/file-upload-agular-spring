package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.DivisionDto;
import com.example.File_Image_upload.entity.Division;
import com.example.File_Image_upload.repository.DivisionRepository;
import com.example.File_Image_upload.service.DivisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class DivisionServiceImpl implements DivisionService {
    private final DivisionRepository divisionRepository;

    @Override
    public List<DivisionDto> getAllDivisions(String searchParams) {
        log.info("Fetching all divisions");

        List<Division> divisions = divisionRepository.findByNameOrCodeContainingIgnoreCase(searchParams);

        return divisions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<DivisionDto> getDivisionByCode(String code) {
        log.info("Fetching division by code: {}", code);

        return divisionRepository.findById(code)
            .map(this::convertToDto);
    }

    @Override
    public Optional<DivisionDto> getDivisionByName(String name) {
        log.info("Fetching division by name: {}", name);

        return divisionRepository.findByNameIgnoreCase(name)
            .map(this::convertToDto);
    }

    @Override
    @Transactional
    public DivisionDto createDivision(DivisionDto divisionDto) {
        log.info("Creating new division: {}", divisionDto.getName());

        if (divisionRepository.existsByCode(divisionDto.getCode())) {
            throw new RuntimeException("Division with code " + divisionDto.getCode() + " already exists");
        }

        Division division = convertToEntity(divisionDto);
        Division savedDivision = divisionRepository.save(division);

        log.info("Division created successfully with code: {}", savedDivision.getCode());
        return convertToDto(savedDivision);
    }

    @Override
    @Transactional
    public DivisionDto updateDivision(String code, DivisionDto divisionDto) {
        log.info("Updating division with code: {}", code);

        Division existingDivision = divisionRepository.findById(code)
            .orElseThrow(() -> new RuntimeException("Division not found with code: " + code));

        existingDivision.setName(divisionDto.getName());

        Division updatedDivision = divisionRepository.save(existingDivision);

        log.info("Division updated successfully: {}", updatedDivision.getCode());
        return convertToDto(updatedDivision);
    }

    @Override
    @Transactional
    public boolean deleteDivision(String code) {
        log.info("Deleting division with code: {}", code);

        if (!divisionRepository.existsByCode(code)) {
            log.warn("Division not found with code: {}", code);
            return false;
        }

        divisionRepository.deleteById(code);
        log.info("Division deleted successfully: {}", code);
        return true;
    }

    @Override
    public boolean existsByCode(String code) {
        return divisionRepository.existsByCode(code);
    }

    @Override
    public List<DivisionDto> searchDivisionsByName(String name) {
        log.info("Searching divisions by name containing: {}", name);

        List<Division> divisions = divisionRepository.findByNameContainingIgnoreCase(name);

        return divisions.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    // Helper method to convert Entity to DTO
    private DivisionDto convertToDto(Division division) {
        DivisionDto dto = new DivisionDto();

        // Generate a hash-based ID from the code for the long id field
        dto.setId(generateIdFromCode(division.getCode()));
        dto.setCode(division.getCode());
        dto.setName(division.getName());
        dto.setDivisionCode(division.getCode()); // Set divisionCode same as code

        return dto;
    }

    // Helper method to convert DTO to Entity
    private Division convertToEntity(DivisionDto divisionDto) {
        Division division = new Division();
        division.setCode(divisionDto.getCode());
        division.setName(divisionDto.getName());
        return division;
    }

    // Helper method to generate consistent long ID from string code
    private long generateIdFromCode(String code) {
        if (code == null || code.isEmpty()) {
            return 0L;
        }

        // Simple approach: convert code to long
        // For codes like "01", "02", etc., this will give 1, 2, etc.
        try {
            return Long.parseLong(code);
        } catch (NumberFormatException e) {
            // If code is not numeric, use hashCode
            return Math.abs(code.hashCode());
        }
    }
}
