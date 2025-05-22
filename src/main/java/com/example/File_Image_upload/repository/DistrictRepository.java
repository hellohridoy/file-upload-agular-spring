package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.District;
import com.example.File_Image_upload.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, String> {

    // Find districts by division code
    @Query("SELECT d FROM District d WHERE d.division.code = :divisionCode ORDER BY d.name")
    List<District> findByDivisionCodeOrderByName(@Param("divisionCode") String divisionCode);

    // Find districts by division code with division details
    @Query("SELECT d FROM District d JOIN FETCH d.division WHERE d.division.code = :divisionCode ORDER BY d.name")
    List<District> findByDivisionCodeWithDivision(@Param("divisionCode") String divisionCode);

    // Find by name (case insensitive)
    Optional<District> findByNameIgnoreCase(String name);

    // Get all districts ordered by code
    @Query("SELECT d FROM District d ORDER BY d.code")
    List<District> findAllOrderByCode();

    // Check if district exists by code
    boolean existsByCode(String code);

    // Find districts by name containing (for search functionality)
    List<District> findByNameContainingIgnoreCase(String name);
}
