package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.dto.DistrictDto;
import com.example.File_Image_upload.service.DistrictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DistrictController {

    private final DistrictService districtService;

    // GET /api/v1/districts - Get all districts
    @GetMapping("/api/v1/districts")
    public ResponseEntity<List<DistrictDto>> getAllDistricts() {
        try {
            log.info("Request received to get all districts");
            List<DistrictDto> districts = districtService.getAllDistricts();
            log.info("Retrieved {} districts", districts.size());
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            log.error("Error retrieving all districts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/districts/by-division/{divisionCode} - Get districts by division code
    @GetMapping("/api/v1/districts/by-division/{divisionCode}")
    public ResponseEntity<List<DistrictDto>> getDistrictsByDivision(@PathVariable String divisionCode) {
        try {
            log.info("Request received to get districts for division: {}", divisionCode);
            List<DistrictDto> districts = districtService.getDistrictsByDivisionCode(divisionCode);
            log.info("Retrieved {} districts for division: {}", districts.size(), divisionCode);
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            log.error("Error retrieving districts for division: {}", divisionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/districts/{code} - Get district by code
    @GetMapping("/api/v1/districts/{code}")
    public ResponseEntity<DistrictDto> getDistrictByCode(@PathVariable String code) {
        try {
            log.info("Request received to get district by code: {}", code);
            Optional<DistrictDto> district = districtService.getDistrictByCode(code);

            return district.map(dto -> {
                log.info("District found: {}", dto.getName());
                return ResponseEntity.ok(dto);
            }).orElseGet(() -> {
                log.warn("District not found with code: {}", code);
                return ResponseEntity.notFound().build();
            });
        } catch (Exception e) {
            log.error("Error retrieving district by code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/districts/search?name=... - Search districts by name
    @GetMapping("/api/v1/districts/search")
    public ResponseEntity<List<DistrictDto>> searchDistrictsByName(@RequestParam String name) {
        try {
            log.info("Request received to search districts by name: {}", name);
            List<DistrictDto> districts = districtService.searchDistrictsByName(name);
            log.info("Found {} districts matching name: {}", districts.size(), name);
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            log.error("Error searching districts by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/v1/districts - Create new district
    @PostMapping("/api/v1/districts")
    public ResponseEntity<DistrictDto> createDistrict(@Valid @RequestBody DistrictDto districtDto) {
        try {
            log.info("Request received to create district: {}", districtDto.getName());
            DistrictDto createdDistrict = districtService.createDistrict(districtDto);
            log.info("District created successfully with code: {}", createdDistrict.getCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDistrict);
        } catch (RuntimeException e) {
            log.error("Business logic error creating district: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating district", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/v1/districts/{code} - Update district
    @PutMapping("/api/v1/districts/{code}")
    public ResponseEntity<DistrictDto> updateDistrict(
        @PathVariable String code,
        @Valid @RequestBody DistrictDto districtDto) {
        try {
            log.info("Request received to update district with code: {}", code);
            DistrictDto updatedDistrict = districtService.updateDistrict(code, districtDto);
            log.info("District updated successfully: {}", updatedDistrict.getName());
            return ResponseEntity.ok(updatedDistrict);
        } catch (RuntimeException e) {
            log.error("District not found for update with code: {}", code);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating district with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/v1/districts/{code} - Delete district
    @DeleteMapping("/api/v1/districts/{code}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable String code) {
        try {
            log.info("Request received to delete district with code: {}", code);
            boolean deleted = districtService.deleteDistrict(code);

            if (deleted) {
                log.info("District deleted successfully with code: {}", code);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("District not found for deletion with code: {}", code);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting district with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
