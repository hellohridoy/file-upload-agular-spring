package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.dto.DivisionDto;
import com.example.File_Image_upload.service.DivisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DivisionController {

    private final DivisionService divisionService;

    // GET /api/v1/divisions - Get all divisions
    @GetMapping("/api/v1/divisions")
    public ResponseEntity<List<DivisionDto>> getAllDivisions(@RequestParam(required = false) String searchParams) {
        try {
            log.info("Request received to get all divisions");
            List<DivisionDto> divisions = divisionService.getAllDivisions(searchParams);
            log.info("Retrieved {} divisions", divisions.size());
            return ResponseEntity.ok(divisions);
        } catch (Exception e) {
            log.error("Error retrieving all divisions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/divisions/{code} - Get division by code
    @GetMapping("/{code}")
    public ResponseEntity<DivisionDto> getDivisionByCode(@PathVariable String code) {
        try {
            log.info("Request received to get division by code: {}", code);
            Optional<DivisionDto> division = divisionService.getDivisionByCode(code);

            return division.map(dto -> {
                log.info("Division found: {}", dto.getName());
                return ResponseEntity.ok(dto);
            }).orElseGet(() -> {
                log.warn("Division not found with code: {}", code);
                return ResponseEntity.notFound().build();
            });
        } catch (Exception e) {
            log.error("Error retrieving division by code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/divisions/search?name=... - Search divisions by name
    @GetMapping("/search")
    public ResponseEntity<List<DivisionDto>> searchDivisionsByName(@RequestParam String name) {
        try {
            log.info("Request received to search divisions by name: {}", name);
            List<DivisionDto> divisions = divisionService.searchDivisionsByName(name);
            log.info("Found {} divisions matching name: {}", divisions.size(), name);
            return ResponseEntity.ok(divisions);
        } catch (Exception e) {
            log.error("Error searching divisions by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/v1/divisions - Create new division
    @PostMapping
    public ResponseEntity<DivisionDto> createDivision(@Valid @RequestBody DivisionDto divisionDto) {
        try {
            log.info("Request received to create division: {}", divisionDto.getName());
            DivisionDto createdDivision = divisionService.createDivision(divisionDto);
            log.info("Division created successfully with code: {}", createdDivision.getCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDivision);
        } catch (RuntimeException e) {
            log.error("Business logic error creating division: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating division", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/v1/divisions/{code} - Update division by code
    @PutMapping("/{code}")
    public ResponseEntity<DivisionDto> updateDivision(
        @PathVariable String code,
        @Valid @RequestBody DivisionDto divisionDto) {
        try {
            log.info("Request received to update division with code: {}", code);
            DivisionDto updatedDivision = divisionService.updateDivision(code, divisionDto);
            log.info("Division updated successfully: {}", updatedDivision.getName());
            return ResponseEntity.ok(updatedDivision);
        } catch (RuntimeException e) {
            log.error("Division not found for update with code: {}", code);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating division with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/v1/divisions/{code} - Delete division by code
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteDivision(@PathVariable String code) {
        try {
            log.info("Request received to delete division with code: {}", code);
            boolean deleted = divisionService.deleteDivision(code);

            if (deleted) {
                log.info("Division deleted successfully with code: {}", code);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Division not found for deletion with code: {}", code);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting division with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/divisions/{code}/exists - Check if division exists
    @GetMapping("/{code}/exists")
    public ResponseEntity<Boolean> checkDivisionExists(@PathVariable String code) {
        try {
            log.info("Request received to check if division exists with code: {}", code);
            boolean exists = divisionService.existsByCode(code);
            log.info("Division exists check for code {}: {}", code, exists);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            log.error("Error checking division existence with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/divisions/by-name/{name} - Get division by exact name
    @GetMapping("/by-name/{name}")
    public ResponseEntity<DivisionDto> getDivisionByName(@PathVariable String name) {
        try {
            log.info("Request received to get division by name: {}", name);
            Optional<DivisionDto> division = divisionService.getDivisionByName(name);

            return division.map(dto -> {
                log.info("Division found by name: {}", dto.getName());
                return ResponseEntity.ok(dto);
            }).orElseGet(() -> {
                log.warn("Division not found with name: {}", name);
                return ResponseEntity.notFound().build();
            });
        } catch (Exception e) {
            log.error("Error retrieving division by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
