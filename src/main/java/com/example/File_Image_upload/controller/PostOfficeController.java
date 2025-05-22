package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.dto.PostOfficeDto;
import com.example.File_Image_upload.service.PostOfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/post-offices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class PostOfficeController {

    private final PostOfficeService postOfficeService;

    // GET /api/v1/post-offices - Get all post offices
    @GetMapping
    public ResponseEntity<List<PostOfficeDto>> getAllPostOffices() {
        try {
            log.info("Request received to get all post offices");
            List<PostOfficeDto> postOffices = postOfficeService.getAllPostOffices();
            log.info("Retrieved {} post offices", postOffices.size());
            return ResponseEntity.ok(postOffices);
        } catch (Exception e) {
            log.error("Error retrieving all post offices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/post-offices/by-upazila/{upazilaCode} - Get post offices by upazila code
    @GetMapping("/by-upazila/{upazilaCode}")
    public ResponseEntity<List<PostOfficeDto>> getPostOfficesByUpazila(@PathVariable String upazilaCode) {
        try {
            log.info("Request received to get post offices for upazila: {}", upazilaCode);
            List<PostOfficeDto> postOffices = postOfficeService.getPostOfficesByUpazilaCode(upazilaCode);
            log.info("Retrieved {} post offices for upazila: {}", postOffices.size(), upazilaCode);
            return ResponseEntity.ok(postOffices);
        } catch (Exception e) {
            log.error("Error retrieving post offices for upazila: {}", upazilaCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/post-offices/by-district/{districtCode} - Get post offices by district code
    @GetMapping("/by-district/{districtCode}")
    public ResponseEntity<List<PostOfficeDto>> getPostOfficesByDistrict(@PathVariable String districtCode) {
        try {
            log.info("Request received to get post offices for district: {}", districtCode);
            List<PostOfficeDto> postOffices = postOfficeService.getPostOfficesByDistrictCode(districtCode);
            log.info("Retrieved {} post offices for district: {}", postOffices.size(), districtCode);
            return ResponseEntity.ok(postOffices);
        } catch (Exception e) {
            log.error("Error retrieving post offices for district: {}", districtCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/post-offices/by-division/{divisionCode} - Get post offices by division code
    @GetMapping("/by-division/{divisionCode}")
    public ResponseEntity<List<PostOfficeDto>> getPostOfficesByDivision(@PathVariable String divisionCode) {
        try {
            log.info("Request received to get post offices for division: {}", divisionCode);
            List<PostOfficeDto> postOffices = postOfficeService.getPostOfficesByDivisionCode(divisionCode);
            log.info("Retrieved {} post offices for division: {}", postOffices.size(), divisionCode);
            return ResponseEntity.ok(postOffices);
        } catch (Exception e) {
            log.error("Error retrieving post offices for division: {}", divisionCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/post-offices/{code} - Get post office by code
    @GetMapping("/{code}")
    public ResponseEntity<PostOfficeDto> getPostOfficeByCode(@PathVariable String code) {
        try {
            log.info("Request received to get post office by code: {}", code);
            Optional<PostOfficeDto> postOffice = postOfficeService.getPostOfficeByCode(code);

            return postOffice.map(dto -> {
                log.info("Post office found: {}", dto.getName());
                return ResponseEntity.ok(dto);
            }).orElseGet(() -> {
                log.warn("Post office not found with code: {}", code);
                return ResponseEntity.notFound().build();
            });
        } catch (Exception e) {
            log.error("Error retrieving post office by code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/post-offices/search?name=... - Search post offices by name
    @GetMapping("/search")
    public ResponseEntity<List<PostOfficeDto>> searchPostOfficesByName(@RequestParam String name) {
        try {
            log.info("Request received to search post offices by name: {}", name);
            List<PostOfficeDto> postOffices = postOfficeService.searchPostOfficesByName(name);
            log.info("Found {} post offices matching name: {}", postOffices.size(), name);
            return ResponseEntity.ok(postOffices);
        } catch (Exception e) {
            log.error("Error searching post offices by name: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/v1/post-offices/search-area?query=... - Search post offices by area
    @GetMapping("/search-area")
    public ResponseEntity<List<PostOfficeDto>> searchPostOfficesByArea(@RequestParam String query) {
        try {
            log.info("Request received to search post offices by area: {}", query);
            List<PostOfficeDto> postOffices = postOfficeService.searchPostOfficesByArea(query);
            log.info("Found {} post offices matching area: {}", postOffices.size(), query);
            return ResponseEntity.ok(postOffices);
        } catch (Exception e) {
            log.error("Error searching post offices by area: {}", query, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/v1/post-offices - Create new post office
    @PostMapping
    public ResponseEntity<PostOfficeDto> createPostOffice(@Valid @RequestBody PostOfficeDto postOfficeDto) {
        try {
            log.info("Request received to create post office: {}", postOfficeDto.getName());
            PostOfficeDto createdPostOffice = postOfficeService.createPostOffice(postOfficeDto);
            log.info("Post office created successfully with code: {}", createdPostOffice.getCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPostOffice);
        } catch (RuntimeException e) {
            log.error("Business logic error creating post office: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating post office", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT /api/v1/post-offices/{code} - Update post office
    @PutMapping("/{code}")
    public ResponseEntity<PostOfficeDto> updatePostOffice(
        @PathVariable String code,
        @Valid @RequestBody PostOfficeDto postOfficeDto) {
        try {
            log.info("Request received to update post office with code: {}", code);
            PostOfficeDto updatedPostOffice = postOfficeService.updatePostOffice(code, postOfficeDto);
            log.info("Post office updated successfully: {}", updatedPostOffice.getName());
            return ResponseEntity.ok(updatedPostOffice);
        } catch (RuntimeException e) {
            log.error("Post office not found for update with code: {}", code);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating post office with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/v1/post-offices/{code} - Delete post office
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deletePostOffice(@PathVariable String code) {
        try {
            log.info("Request received to delete post office with code: {}", code);
            boolean deleted = postOfficeService.deletePostOffice(code);

            if (deleted) {
                log.info("Post office deleted successfully with code: {}", code);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("Post office not found for deletion with code: {}", code);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting post office with code: {}", code, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
