package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.PostOfficeDto;
import com.example.File_Image_upload.entity.PostOffice;
import com.example.File_Image_upload.entity.Upazila;
import com.example.File_Image_upload.repository.PostOfficeRepository;
import com.example.File_Image_upload.repository.UpazilaRepository;
import com.example.File_Image_upload.service.PostOfficeService;
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
public class PostOfficeServiceImpl implements PostOfficeService {

    private final PostOfficeRepository postOfficeRepository;
    private final UpazilaRepository upazilaRepository;

    @Override
    public List<PostOfficeDto> getAllPostOffices() {
        log.info("Fetching all post offices");

        List<PostOffice> postOffices = postOfficeRepository.findAllOrderByCode();

        return postOffices.stream()
            .map(postOffice -> convertToDto(postOffice))
            .collect(Collectors.toList());
    }

    @Override
    public List<PostOfficeDto> getPostOfficesByUpazilaCode(String upazilaCode) {
        log.info("Fetching post offices for upazila code: {}", upazilaCode);

        List<PostOffice> postOffices = postOfficeRepository.findByUpazilaCodeWithDetails(upazilaCode);

        return postOffices.stream()
            .map(postOffice -> convertToDto(postOffice))
            .collect(Collectors.toList());
    }

    @Override
    public List<PostOfficeDto> getPostOfficesByDistrictCode(String districtCode) {
        log.info("Fetching post offices for district code: {}", districtCode);

        List<PostOffice> postOffices = postOfficeRepository.findByDistrictCodeOrderByUpazilaAndName(districtCode);

        return postOffices.stream()
            .map(postOffice -> convertToDto(postOffice))
            .collect(Collectors.toList());
    }

    @Override
    public List<PostOfficeDto> getPostOfficesByDivisionCode(String divisionCode) {
        log.info("Fetching post offices for division code: {}", divisionCode);

        List<PostOffice> postOffices = postOfficeRepository.findByDivisionCodeOrderByHierarchy(divisionCode);

        return postOffices.stream()
            .map(postOffice -> convertToDto(postOffice))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<PostOfficeDto> getPostOfficeByCode(String code) {
        log.info("Fetching post office by code: {}", code);

        Optional<PostOffice> postOffice = postOfficeRepository.findByNameIgnoreCase(code);
        return postOffice.map(this::convertToDto);
    }

    @Override
    public Optional<PostOfficeDto> getPostOfficeByName(String name) {
        log.info("Fetching post office by name: {}", name);

        Optional<PostOffice> postOffice = postOfficeRepository.findByNameIgnoreCase(name);
        if (postOffice.isPresent()) {
            return Optional.of(convertToDto(postOffice.get()));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public PostOfficeDto createPostOffice(PostOfficeDto postOfficeDto) {
        log.info("Creating new post office: {}", postOfficeDto.getName());

        // Validate input
        if (postOfficeDto.getCode() == null || postOfficeDto.getCode().trim().isEmpty()) {
            throw new RuntimeException("Post office code is required");
        }

        if (postOfficeDto.getName() == null || postOfficeDto.getName().trim().isEmpty()) {
            throw new RuntimeException("Post office name is required");
        }

        if (postOfficeDto.getUpazilaCode() == null || postOfficeDto.getUpazilaCode().trim().isEmpty()) {
            throw new RuntimeException("Upazila code is required");
        }

        // Check if post office already exists
        if (postOfficeRepository.existsByCode(postOfficeDto.getCode())) {
            throw new RuntimeException("Post office with code " + postOfficeDto.getCode() + " already exists");
        }

        // Verify upazila exists
        Upazila upazila = upazilaRepository.findById(postOfficeDto.getUpazilaCode())
            .orElseThrow(() -> new RuntimeException("Upazila not found with code: " + postOfficeDto.getUpazilaCode()));

        // Create post office entity
        PostOffice postOffice = new PostOffice();
        postOffice.setCode(postOfficeDto.getCode());
        postOffice.setName(postOfficeDto.getName());
        postOffice.setUpazila(upazila);

        // Save post office
        PostOffice savedPostOffice = postOfficeRepository.save(postOffice);

        log.info("Post office created successfully with code: {}", savedPostOffice.getCode());
        return convertToDto(savedPostOffice);
    }

    @Override
    @Transactional
    public PostOfficeDto updatePostOffice(String code, PostOfficeDto postOfficeDto) {
        log.info("Updating post office with code: {}", code);

        // Find existing post office
        PostOffice existingPostOffice = postOfficeRepository.findByNameIgnoreCase(code)
            .orElseThrow(() -> new RuntimeException("Post office not found with code: " + code));

        // Update fields
        if (postOfficeDto.getName() != null && !postOfficeDto.getName().trim().isEmpty()) {
            existingPostOffice.setName(postOfficeDto.getName());
        }

        // Update upazila if provided
        if (postOfficeDto.getUpazilaCode() != null && !postOfficeDto.getUpazilaCode().trim().isEmpty()) {
            Upazila upazila = upazilaRepository.findById(postOfficeDto.getUpazilaCode())
                .orElseThrow(() -> new RuntimeException("Upazila not found with code: " + postOfficeDto.getUpazilaCode()));
            existingPostOffice.setUpazila(upazila);
        }

        // Save updated post office
        PostOffice updatedPostOffice = postOfficeRepository.save(existingPostOffice);

        log.info("Post office updated successfully: {}", updatedPostOffice.getCode());
        return convertToDto(updatedPostOffice);
    }

    @Override
    @Transactional
    public boolean deletePostOffice(String code) {
        log.info("Deleting post office with code: {}", code);

        if (!postOfficeRepository.existsByCode(code)) {
            log.warn("Post office not found with code: {}", code);
            return false;
        }

        postOfficeRepository.existsByCode(code);
        log.info("Post office deleted successfully: {}", code);
        return true;
    }

    @Override
    public boolean existsByCode(String code) {
        return postOfficeRepository.existsByCode(code);
    }

    @Override
    public List<PostOfficeDto> searchPostOfficesByName(String name) {
        log.info("Searching post offices by name containing: {}", name);

        List<PostOffice> postOffices = postOfficeRepository.findByNameContainingIgnoreCase(name);

        return postOffices.stream()
            .map(postOffice -> convertToDto(postOffice))
            .collect(Collectors.toList());
    }

    @Override
    public List<PostOfficeDto> searchPostOfficesByArea(String searchTerm) {
        log.info("Searching post offices by area containing: {}", searchTerm);

        List<PostOffice> postOffices = postOfficeRepository.searchByAreaName(searchTerm);

        return postOffices.stream()
            .map(postOffice -> convertToDto(postOffice))
            .collect(Collectors.toList());
    }

    // Helper method to convert Entity to DTO
    private PostOfficeDto convertToDto(PostOffice postOffice) {
        if (postOffice == null) {
            return null;
        }

        PostOfficeDto dto = new PostOfficeDto();

        // Generate ID from code
        dto.setId(generateIdFromCode(postOffice.getCode()));
        dto.setCode(postOffice.getCode());
        dto.setName(postOffice.getName());

        // Set upazila information
        if (postOffice.getUpazila() != null) {
            dto.setUpazilaCode(postOffice.getUpazila().getCode());
            dto.setUpazilaName(postOffice.getUpazila().getName());

            // Set district information
            if (postOffice.getUpazila().getDistrict() != null) {
                dto.setDistrictCode(postOffice.getUpazila().getDistrict().getCode());
                dto.setDistrictName(postOffice.getUpazila().getDistrict().getName());

                // Set division information (for breadcrumb navigation)
                if (postOffice.getUpazila().getDistrict().getDivision() != null) {
                    dto.setDivisionCode(postOffice.getUpazila().getDistrict().getDivision().getCode());
                    dto.setDivisionName(postOffice.getUpazila().getDistrict().getDivision().getName());
                }
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
