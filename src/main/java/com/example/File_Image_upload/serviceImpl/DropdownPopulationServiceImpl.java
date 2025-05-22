package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.DropdownPopulationResponse;
import com.example.File_Image_upload.entity.PostOffice;
import com.example.File_Image_upload.repository.PostOfficeRepository;
import com.example.File_Image_upload.service.BacktrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DropdownPopulationServiceImpl implements BacktrackingService {

    private final PostOfficeRepository postOfficeRepository;

    @Override
    public Optional<DropdownPopulationResponse> getParentObjectsForDropdown(String postOfficeName) {
        log.info("Getting parent objects for dropdown auto-population: {}", postOfficeName);

        Optional<PostOffice> postOffice = findPostOfficeWithHierarchy(postOfficeName);

        if (postOffice.isPresent()) {
            return Optional.of(buildDropdownPopulationResponse(postOffice.get()));
        }

        log.warn("Post office not found: {}", postOfficeName);
        return Optional.empty();
    }

    @Override
    public Optional<DropdownPopulationResponse> getParentObjectsByCode(String postOfficeCode) {
        log.info("Getting parent objects by code for dropdown auto-population: {}", postOfficeCode);

        Optional<PostOffice> postOffice = postOfficeRepository.findByCodeWithHierarchy(postOfficeCode);

        if (postOffice.isPresent()) {
            return Optional.of(buildDropdownPopulationResponse(postOffice.get()));
        }

        log.warn("Post office not found with code: {}", postOfficeCode);
        return Optional.empty();
    }

    // Helper method to find post office with hierarchy
    private Optional<PostOffice> findPostOfficeWithHierarchy(String postOfficeName) {
        try {
            return postOfficeRepository.findByNameIgnoreCaseWithHierarchy(postOfficeName);
        } catch (Exception e) {
            log.warn("Error loading with hierarchy, falling back to simple query: {}", e.getMessage());
            return postOfficeRepository.findByNameIgnoreCase(postOfficeName);
        }
    }

    // Build the dropdown population response
    private DropdownPopulationResponse buildDropdownPopulationResponse(PostOffice postOffice) {
        DropdownPopulationResponse response = new DropdownPopulationResponse();

        // Post Office Info
        response.setPostOffice(new DropdownPopulationResponse.PostOfficeDropdownDto(
            generateIdFromCode(postOffice.getCode()),
            postOffice.getCode(),
            postOffice.getName(),
            postOffice.getName() + " (" + postOffice.getCode() + ")"
        ));

        if (postOffice.getUpazila() != null) {
            // Parent Upazila Info
            response.setUpazila(new DropdownPopulationResponse.UpazilaDropdownDto(
                generateIdFromCode(postOffice.getUpazila().getCode()),
                postOffice.getUpazila().getCode(),
                postOffice.getUpazila().getName(),
                postOffice.getUpazila().getDistrict() != null ? postOffice.getUpazila().getDistrict().getCode() : "",
                postOffice.getUpazila().getName() + " (" + postOffice.getUpazila().getCode() + ")",
                true // Should auto-select this upazila
            ));

            if (postOffice.getUpazila().getDistrict() != null) {
                // Parent District Info
                response.setDistrict(new DropdownPopulationResponse.DistrictDropdownDto(
                    generateIdFromCode(postOffice.getUpazila().getDistrict().getCode()),
                    postOffice.getUpazila().getDistrict().getCode(),
                    postOffice.getUpazila().getDistrict().getName(),
                    postOffice.getUpazila().getDistrict().getDivision() != null ?
                        postOffice.getUpazila().getDistrict().getDivision().getCode() : "",
                    postOffice.getUpazila().getDistrict().getName() + " (" + postOffice.getUpazila().getDistrict().getCode() + ")",
                    true // Should auto-select this district
                ));

                if (postOffice.getUpazila().getDistrict().getDivision() != null) {
                    // Parent Division Info
                    response.setDivision(new DropdownPopulationResponse.DivisionDropdownDto(
                        generateIdFromCode(postOffice.getUpazila().getDistrict().getDivision().getCode()),
                        postOffice.getUpazila().getDistrict().getDivision().getCode(),
                        postOffice.getUpazila().getDistrict().getDivision().getName(),
                        postOffice.getUpazila().getDistrict().getDivision().getName() + " (" + postOffice.getUpazila().getDistrict().getDivision().getCode() + ")",
                        true // Should auto-select this division
                    ));
                }
            }
        }

        // Auto-set instructions
        response.setAutoSetInstructions(buildAutoSetInstructions(postOffice));

        return response;
    }

    // Build auto-set instructions
    private DropdownPopulationResponse.AutoSetInstructions buildAutoSetInstructions(PostOffice postOffice) {
        String divisionCode = "";
        String districtCode = "";
        String upazilaCode = "";

        if (postOffice.getUpazila() != null) {
            upazilaCode = postOffice.getUpazila().getCode();

            if (postOffice.getUpazila().getDistrict() != null) {
                districtCode = postOffice.getUpazila().getDistrict().getCode();

                if (postOffice.getUpazila().getDistrict().getDivision() != null) {
                    divisionCode = postOffice.getUpazila().getDistrict().getDivision().getCode();
                }
            }
        }

        return new DropdownPopulationResponse.AutoSetInstructions(
            divisionCode,
            districtCode,
            upazilaCode,
            postOffice.getCode(),
            "Auto-select the provided codes in respective dropdowns"
        );
    }

    // Generate ID from code
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
