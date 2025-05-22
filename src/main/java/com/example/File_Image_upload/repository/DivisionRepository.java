package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, String> {

    // Find by name (case insensitive)
    Optional<Division> findByNameIgnoreCase(String name);

    @Query("SELECT d FROM Division d " +
        "WHERE (:search IS NULL OR :search = '' " +
        "   OR LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
        "   OR LOWER(d.code) LIKE LOWER(CONCAT('%', :search, '%'))) " +
        "ORDER BY d.code")
    List<Division> findByNameOrCodeContainingIgnoreCase(@Param("search") String search);



    // Check if division exists by code
    boolean existsByCode(String code);

    // Find divisions by name containing (for search functionality)
    List<Division> findByNameContainingIgnoreCase(String name);
}
