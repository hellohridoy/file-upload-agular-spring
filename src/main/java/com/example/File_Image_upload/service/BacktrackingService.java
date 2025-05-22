package com.example.File_Image_upload.service;

import com.example.File_Image_upload.dto.DropdownPopulationResponse;
import java.util.List;
import java.util.Optional;

public interface BacktrackingService {

    /**
     * Get parent hierarchy objects for dropdown auto-population
     * @param postOfficeName the post office name to search for
     * @return DropdownPopulationResponse with parent objects
     */
    Optional<DropdownPopulationResponse> getParentObjectsForDropdown(String postOfficeName);

    /**
     * Get parent hierarchy objects by post office code
     * @param postOfficeCode the post office code
     * @return DropdownPopulationResponse with parent objects
     */
    Optional<DropdownPopulationResponse> getParentObjectsByCode(String postOfficeCode);
}

