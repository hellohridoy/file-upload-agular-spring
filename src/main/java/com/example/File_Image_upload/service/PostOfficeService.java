package com.example.File_Image_upload.service;
import com.example.File_Image_upload.dto.PostOfficeDto;
import com.example.File_Image_upload.entity.PostOffice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostOfficeService {

    // Get all post offices
    List<PostOfficeDto> getAllPostOffices();

    // Get post offices by upazila code
    List<PostOfficeDto> getPostOfficesByUpazilaCode(String upazilaCode);

    // Get post offices by district code
    List<PostOfficeDto> getPostOfficesByDistrictCode(String districtCode);

    // Get post offices by division code
    List<PostOfficeDto> getPostOfficesByDivisionCode(String divisionCode);

    // Get post office by code
    Optional<PostOfficeDto> getPostOfficeByCode(String code);

    // Get post office by name
    Optional<PostOfficeDto> getPostOfficeByName(String name);

    // Create new post office
    PostOfficeDto createPostOffice(PostOfficeDto postOfficeDto);

    // Update post office
    PostOfficeDto updatePostOffice(String code, PostOfficeDto postOfficeDto);

    // Delete post office
    boolean deletePostOffice(String code);

    // Check if post office exists
    boolean existsByCode(String code);

    // Search post offices by name
    List<PostOfficeDto> searchPostOfficesByName(String name);

    // Search post offices by area (comprehensive search)
    List<PostOfficeDto> searchPostOfficesByArea(String searchTerm);
}
