package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.Upazila;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UpazilaRepository extends JpaRepository<Upazila, String> {

    // Find upazilas by district code
    @Query("SELECT u FROM Upazila u WHERE u.district.code = :districtCode ORDER BY u.name")
    List<Upazila> findByDistrictCodeOrderByName(@Param("districtCode") String districtCode);

    // Find upazilas by district code with district and division details
    @Query("SELECT u FROM Upazila u JOIN FETCH u.district d JOIN FETCH d.division WHERE u.district.code = :districtCode ORDER BY u.name")
    List<Upazila> findByDistrictCodeWithDetails(@Param("districtCode") String districtCode);

    // Find upazilas by division code (for getting all upazilas in a division)
    @Query("SELECT u FROM Upazila u JOIN FETCH u.district d JOIN FETCH d.division div WHERE div.code = :divisionCode ORDER BY d.name, u.name")
    List<Upazila> findByDivisionCodeOrderByDistrictAndName(@Param("divisionCode") String divisionCode);

    // Find by name (case insensitive)
    Optional<Upazila> findByNameIgnoreCase(String name);

    // Get all upazilas ordered by code
    @Query("SELECT u FROM Upazila u ORDER BY u.code")
    List<Upazila> findAllOrderByCode();

    // Check if upazila exists by code
    boolean existsByCode(String code);

    // Find upazilas by name containing (for search functionality)
    List<Upazila> findByNameContainingIgnoreCase(String name);
}
