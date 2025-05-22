package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.dto.DropdownPopulationResponse;
import com.example.File_Image_upload.entity.PostOffice;
import com.example.File_Image_upload.repository.PostOfficeRepository;
import com.example.File_Image_upload.service.BacktrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DropdownPopulationController {

    private final BacktrackingService dropdownPopulationService;

    private final PostOfficeRepository postOfficeRepository;

    // GET /api/v1/dropdown/auto-populate?postOffice=Charpara
    @GetMapping("/api/v1/dropdown/auto-populate")
    public ResponseEntity<DropdownPopulationResponse> getParentObjectsForDropdown(
        @RequestParam("postOffice") String postOfficeName) {
        try {
            log.info("Request to get parent objects for dropdown auto-population: {}", postOfficeName);
            Optional<DropdownPopulationResponse> response = dropdownPopulationService.getParentObjectsForDropdown(postOfficeName);

            return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting parent objects for dropdown: {}", postOfficeName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/dropdown/auto-populate-by-code?code=08020803
    @GetMapping("/api/v1/dropdown/auto-populate-by-code")
    public ResponseEntity<DropdownPopulationResponse> getParentObjectsByCode(
        @RequestParam("code") String postOfficeCode) {
        try {
            log.info("Request to get parent objects by code for dropdown auto-population: {}", postOfficeCode);
            Optional<DropdownPopulationResponse> response = dropdownPopulationService.getParentObjectsByCode(postOfficeCode);

            return response.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting parent objects by code for dropdown: {}", postOfficeCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/dropdown/division-only?postOffice=Charpara
    @GetMapping("/api/v1/dropdown/division-only")
    public ResponseEntity<DropdownPopulationResponse.DivisionDropdownDto> getDivisionOnly(
        @RequestParam("postOffice") String postOfficeName) {
        try {
            Optional<DropdownPopulationResponse> response = dropdownPopulationService.getParentObjectsForDropdown(postOfficeName);

            return response.map(r -> ResponseEntity.ok(r.getDivision()))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting division only: {}", postOfficeName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/dropdown/district-only?postOffice=Charpara
    @GetMapping("/api/v1/dropdown/district-only")
    public ResponseEntity<DropdownPopulationResponse.DistrictDropdownDto> getDistrictOnly(
        @RequestParam("postOffice") String postOfficeName) {
        try {
            Optional<DropdownPopulationResponse> response = dropdownPopulationService.getParentObjectsForDropdown(postOfficeName);

            return response.map(r -> ResponseEntity.ok(r.getDistrict()))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting district only: {}", postOfficeName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/dropdown/upazila-only?postOffice=Charpara
    @GetMapping("/api/v1/dropdown/upazila-only")
    public ResponseEntity<DropdownPopulationResponse.UpazilaDropdownDto> getUpazilaOnly(
        @RequestParam("postOffice") String postOfficeName) {
        try {
            Optional<DropdownPopulationResponse> response = dropdownPopulationService.getParentObjectsForDropdown(postOfficeName);

            return response.map(r -> ResponseEntity.ok(r.getUpazila()))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting upazila only: {}", postOfficeName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Test if repository methods work
    @GetMapping("/api/v1/dropdown/repository-methods")
    public ResponseEntity<Map<String, Object>> testRepositoryMethods() {
        Map<String, Object> results = new HashMap<>();

        try {
            // Test 1: Basic findById
            Optional<PostOffice> basic = postOfficeRepository.findById(Long.valueOf("08020803"));
            results.put("basicFindById", basic.isPresent() ? "SUCCESS" : "NOT FOUND");

            // Test 2: Find by name (simple)
            Optional<PostOffice> byName = postOfficeRepository.findByNameIgnoreCase("Charpara");
            results.put("findByNameSimple", byName.isPresent() ? "SUCCESS" : "NOT FOUND");

            // Test 3: Find by code with hierarchy (the missing method)
            try {
                Optional<PostOffice> withHierarchy = postOfficeRepository.findByCodeWithHierarchy("08020803");
                results.put("findByCodeWithHierarchy", withHierarchy.isPresent() ? "SUCCESS" : "NOT FOUND");

                if (withHierarchy.isPresent()) {
                    PostOffice po = withHierarchy.get();
                    results.put("hierarchyInfo", Map.of(
                        "postOffice", po.getName(),
                        "upazila", po.getUpazila() != null ? po.getUpazila().getName() : "NULL",
                        "district", po.getUpazila() != null && po.getUpazila().getDistrict() != null ?
                            po.getUpazila().getDistrict().getName() : "NULL",
                        "division", po.getUpazila() != null && po.getUpazila().getDistrict() != null &&
                            po.getUpazila().getDistrict().getDivision() != null ?
                            po.getUpazila().getDistrict().getDivision().getName() : "NULL"
                    ));
                }
            } catch (Exception e) {
                results.put("findByCodeWithHierarchy", "ERROR: " + e.getMessage());
            }

            // Test 4: Safe method
            try {
                Optional<PostOffice> safe = postOfficeRepository.findByCodeWithHierarchySafe("08020803");
                results.put("findByCodeWithHierarchySafe", safe.isPresent() ? "SUCCESS" : "NOT FOUND");
            } catch (Exception e) {
                results.put("findByCodeWithHierarchySafe", "ERROR: " + e.getMessage());
            }

            results.put("overallStatus", "TESTS COMPLETED");

        } catch (Exception e) {
            results.put("error", e.getMessage());
            results.put("overallStatus", "TESTS FAILED");
        }

        return ResponseEntity.ok(results);
    }

    // Test the dropdown service
    @GetMapping("/api/v1/dropdown/dropdown-service")
    public ResponseEntity<Object> testDropdownService() {
        try {
            Optional<DropdownPopulationResponse> response = dropdownPopulationService.getParentObjectsForDropdown("Charpara");

            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                return ResponseEntity.ok(Map.of("status", "NOT FOUND", "message", "Charpara not found in database"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    // Test with post office code
    @GetMapping("/api/v1/dropdown/dropdown-by-code/{code}")
    public ResponseEntity<Object> testDropdownByCode(@PathVariable String code) {
        try {
            Optional<DropdownPopulationResponse> response = dropdownPopulationService.getParentObjectsByCode(code);

            if (response.isPresent()) {
                return ResponseEntity.ok(response.get());
            } else {
                return ResponseEntity.ok(Map.of("status", "NOT FOUND", "message", "Post office code not found: " + code));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    // Check if Charpara exists in database
    @GetMapping("/api/v1/dropdown/check-charpara")
    public ResponseEntity<Object> checkCharpara() {
        Map<String, Object> result = new HashMap<>();

        // Check if Charpara exists
        Optional<PostOffice> charpara = postOfficeRepository.findByNameIgnoreCase("Charpara");
        result.put("charparaExists", charpara.isPresent());

        if (charpara.isPresent()) {
            PostOffice po = charpara.get();
            result.put("charparaInfo", Map.of(
                "code", po.getCode(),
                "name", po.getName()
            ));

            // Try to access hierarchy
            try {
                if (po.getUpazila() != null) {
                    result.put("upazilaName", po.getUpazila().getName());
                    if (po.getUpazila().getDistrict() != null) {
                        result.put("districtName", po.getUpazila().getDistrict().getName());
                        if (po.getUpazila().getDistrict().getDivision() != null) {
                            result.put("divisionName", po.getUpazila().getDistrict().getDivision().getName());
                        }
                    }
                }
            } catch (Exception e) {
                result.put("hierarchyError", e.getMessage());
            }
        } else {
            // Check what post offices exist
            result.put("totalPostOffices", postOfficeRepository.count());
            result.put("suggestion", "Run the INSERT queries to populate post offices data");
        }

        return ResponseEntity.ok(result);
    }
}
