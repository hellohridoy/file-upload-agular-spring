package com.example.File_Image_upload.controller;
import com.example.File_Image_upload.dto.UpazilaDto;
import com.example.File_Image_upload.service.UpazilaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/upazilas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class UpazilaController {

    private final UpazilaService upazilaService;

    // GET /api/v1/upazilas - Get all upazilas
    @GetMapping
    public ResponseEntity<List<UpazilaDto>> getAllUpazilas() {
        try {
            log.info("Request received to get all upazilas");
            List<UpazilaDto> upazilas = upazilaService.getAllUpazilas();
            log.info("Retrieved {} upazilas", upazilas.size());
            return ResponseEntity.ok(upazilas);
        } catch (Exception e) {
            log.error("Error retrieving all upazilas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/upazilas/by-district/{districtCode} - Get upazilas by district code
    @GetMapping("/by-district/{districtCode}")
    public ResponseEntity<List<UpazilaDto>> getUpazilasByDistrict(@PathVariable String districtCode) {
        try {
            log.info("Request received to get upazilas for district: {}", districtCode);
            List<UpazilaDto> upazilas = upazilaService.getUpazilasByDistrictCode(districtCode);
            log.info("Retrieved {} upazilas for district: {}", upazilas.size(), districtCode);
            return ResponseEntity.ok(upazilas);
        } catch (Exception e) {
            log.error("Error retrieving upazilas for district: {}", districtCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/upazilas/by-division/{divisionCode} - Get upazilas by division code
    @GetMapping("/by-division/{divisionCode}")
    public ResponseEntity<List<UpazilaDto>> getUpazilasByDivision(@PathVariable String divisionCode) {
        try {
            log.info("Request received to get upazilas for division: {}", divisionCode);
            List<UpazilaDto> upazilas = upazilaService.getUpazilasByDivisionCode(divisionCode);
            log.info("Retrieved {} upazilas for division: {}", upazilas.size(), divisionCode);
            return ResponseEntity.ok(upazilas);
        } catch (Exception e) {
            log.error("Error retrieving upazilas for division: {}", divisionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/upazilas/{code} - Get upazila by code
    @GetMapping("/{code}")
    public ResponseEntity<UpazilaDto> getUpazilaByCode(@PathVariable String code) {
        try {
            log.info("Request received to get upazila by code: {}", code);
            Optional<UpazilaDto> upazila = upazilaService.getUpazilaByCode(code);

            return upazila.map(dto -> {
                log.info("Upazila found: {}", dto.getName());
                return ResponseEntity.ok(dto);
            }).orElseGet(() -> {
                log.warn("Upazila not found with code: {}", code);
                return ResponseEntity.notFound().build();
            });
        } catch (Exception e) {
            log.error("Error retrieving upazila by code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/upazilas/search?name=... - Search upazilas by name
    @GetMapping("/search")
    public ResponseEntity<List<UpazilaDto>> searchUpazilasByName(@RequestParam String name) {
        try {
            log.info("Request received to search upazilas by name: {}", name);
            List<UpazilaDto> upazilas = upazilaService.searchUpazilasByName(name);
            log.info("Found {} upazilas matching name: {}", upazilas.size(), name);
            return ResponseEntity.ok(upazilas);
        } catch (Exception e) {
            log.error("Error searching upazilas by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/v1/upazilas - Create new upazila
    @PostMapping
    public ResponseEntity<UpazilaDto> createUpazila(@Valid @RequestBody UpazilaDto upazilaDto) {
        try {
            log.info("Request received to create upazila: {}", upazilaDto.getName());
            UpazilaDto createdUpazila = upazilaService.createUpazila(upazilaDto);
            log.info("Upazila created successfully with code: {}", createdUpazila.getCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUpazila);
        } catch (RuntimeException e) {
            log.error("Business logic error creating upazila: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating upazila", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/v1/upazilas/{code} - Update upazila
    @PutMapping("/{code}")
    public ResponseEntity<UpazilaDto> updateUpazila(
        @PathVariable String code,
        @Valid @RequestBody UpazilaDto upazilaDto) {
        try {
            log.info("Request received to update upazila with code: {}", code);
            UpazilaDto updatedUpazila = upazilaService.updateUpazila(code, upazilaDto);
            log.info("Upazila updated successfully: {}", updatedUpazila.getName());
            return ResponseEntity.ok(updatedUpazila);
        } catch (RuntimeException e) {
            log.error("Upazila not found for update with code: {}", code);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating upazila with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/v1/upazilas/{code} - Delete upazila
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteUpazila(@PathVariable String code) {
        try {
            log.info("Request received to delete upazila with code: {}", code);
            boolean deleted = upazilaService.deleteUpazila(code);

            if (deleted) {
                log.info("Upazila deleted successfully with code: {}", code);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Upazila not found for deletion with code: {}", code);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting upazila with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
