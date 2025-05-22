package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostOfficeRepository extends JpaRepository<PostOffice, Long> {

    // =====================================================================
    // BASIC CRUD METHODS (Inherited from JpaRepository)
    // =====================================================================
    // PostOffice save(PostOffice entity);
    // Optional<PostOffice> findById(String id);
    // void deleteById(String id);
    // List<PostOffice> findAll();
    // boolean existsById(String id);

    // =====================================================================
    // SIMPLE FIND METHODS (WITHOUT HIERARCHY)
    // =====================================================================

    /**
     * Find post office by name (case insensitive)
     */
    Optional<PostOffice> findByNameIgnoreCase(String name);

    /**
     * Find post offices by name containing (case insensitive)
     */
    List<PostOffice> findByNameContainingIgnoreCase(String name);

    /**
     * Check if post office exists by code
     */
    boolean existsByCode(String code);

    // =====================================================================
    // HIERARCHY-LOADED FIND METHODS (WITH FETCH JOIN)
    // =====================================================================

    /**
     * Find post office by name with complete hierarchy loaded (FETCH JOIN)
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division " +
        "WHERE LOWER(p.name) = LOWER(:name)")
    Optional<PostOffice> findByNameIgnoreCaseWithHierarchy(@Param("name") String name);

    /**
     * Find post offices by name containing with hierarchy loaded
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division " +
        "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PostOffice> findByNameContainingIgnoreCaseWithHierarchy(@Param("name") String name);
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division " +
        "WHERE p.upazila.code = :upazilaCode " +
        "ORDER BY p.name")
    List<PostOffice> findByUpazilaCodeWithDetails(@Param("upazilaCode") String upazilaCode);
    /**
     * Find post office by code with hierarchy loaded - THIS WAS MISSING!
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division " +
        "WHERE p.code = :code")
    Optional<PostOffice> findByCodeWithHierarchy(@Param("code") String code);

    /**
     * Find post office by ID with hierarchy loaded (alternative to above)
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division " +
        "WHERE p.code = :id")
    Optional<PostOffice> findByIdWithHierarchy(@Param("id") String id);

    // =====================================================================
    // UPAZILA/DISTRICT/DIVISION FILTERING METHODS
    // =====================================================================

    /**
     * Find post offices by upazila code (simple)
     */
    @Query("SELECT p FROM PostOffice p WHERE p.upazila.code = :upazilaCode ORDER BY p.name")
    List<PostOffice> findByUpazilaCodeOrderByName(@Param("upazilaCode") String upazilaCode);

    /**
     * Find post offices by upazila code with hierarchy loaded
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division " +
        "WHERE p.upazila.code = :upazilaCode " +
        "ORDER BY p.name")
    List<PostOffice> findByUpazilaCodeWithHierarchy(@Param("upazilaCode") String upazilaCode);

    /**
     * Find post offices by district code
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN p.upazila u " +
        "JOIN u.district d " +
        "WHERE d.code = :districtCode " +
        "ORDER BY u.name, p.name")
    List<PostOffice> findByDistrictCodeOrderByUpazilaAndName(@Param("districtCode") String districtCode);

    /**
     * Find post offices by district code with hierarchy loaded
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division " +
        "WHERE d.code = :districtCode " +
        "ORDER BY u.name, p.name")
    List<PostOffice> findByDistrictCodeWithHierarchy(@Param("districtCode") String districtCode);

    /**
     * Find post offices by division code
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN p.upazila u " +
        "JOIN u.district d " +
        "JOIN d.division div " +
        "WHERE div.code = :divisionCode " +
        "ORDER BY d.name, u.name, p.name")
    List<PostOffice> findByDivisionCodeOrderByHierarchy(@Param("divisionCode") String divisionCode);

    /**
     * Find post offices by division code with hierarchy loaded
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division div " +
        "WHERE div.code = :divisionCode " +
        "ORDER BY d.name, u.name, p.name")
    List<PostOffice> findByDivisionCodeWithHierarchy(@Param("divisionCode") String divisionCode);

    // =====================================================================
    // SEARCH METHODS
    // =====================================================================

    /**
     * Search by area name (upazila, district, or division) name
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN p.upazila u " +
        "JOIN u.district d " +
        "JOIN d.division div " +
        "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(div.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
        "ORDER BY p.name")
    List<PostOffice> searchByAreaName(@Param("searchTerm") String searchTerm);

    /**
     * Search by area name with hierarchy loaded
     */
    @Query("SELECT p FROM PostOffice p " +
        "JOIN FETCH p.upazila u " +
        "JOIN FETCH u.district d " +
        "JOIN FETCH d.division div " +
        "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
        "LOWER(div.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
        "ORDER BY div.name, d.name, u.name, p.name")
    List<PostOffice> searchByAreaNameWithHierarchy(@Param("searchTerm") String searchTerm);

    // =====================================================================
    // ALTERNATIVE SIMPLE METHODS (Fallback options)
    // =====================================================================

    /**
     * Simple find by upazila code using Spring Data naming convention
     */
    List<PostOffice> findByUpazilaCode(String upazilaCode);

    /**
     * Simple find by upazila code ordered by name
     */

    // =====================================================================
    // UTILITY METHODS
    // =====================================================================

    /**
     * Get all post offices ordered by code
     */
    @Query("SELECT p FROM PostOffice p ORDER BY p.code")
    List<PostOffice> findAllOrderByCode();

    /**
     * Count post offices by division
     */
    @Query("SELECT div.name, COUNT(p) FROM PostOffice p " +
        "JOIN p.upazila u " +
        "JOIN u.district d " +
        "JOIN d.division div " +
        "GROUP BY div.code, div.name " +
        "ORDER BY div.name")
    List<Object[]> countPostOfficesByDivision();

    /**
     * Find post offices with incomplete hierarchy (for data validation)
     */
    @Query("SELECT p FROM PostOffice p " +
        "LEFT JOIN p.upazila u " +
        "LEFT JOIN u.district d " +
        "LEFT JOIN d.division div " +
        "WHERE u IS NULL OR d IS NULL OR div IS NULL")
    List<PostOffice> findPostOfficesWithIncompleteHierarchy();

    // =====================================================================
    // SAFE METHODS WITH TRY-CATCH ALTERNATIVES
    // =====================================================================

    /**
     * Safe method to find by name with hierarchy (with fallback)
     * Use this if you're having issues with FETCH JOIN
     */
    @Query("SELECT p FROM PostOffice p " +
        "LEFT JOIN FETCH p.upazila u " +
        "LEFT JOIN FETCH u.district d " +
        "LEFT JOIN FETCH d.division " +
        "WHERE LOWER(p.name) = LOWER(:name)")
    Optional<PostOffice> findByNameIgnoreCaseWithHierarchySafe(@Param("name") String name);

    /**
     * Safe method to find by code with hierarchy (with fallback)
     */
    @Query("SELECT p FROM PostOffice p " +
        "LEFT JOIN FETCH p.upazila u " +
        "LEFT JOIN FETCH u.district d " +
        "LEFT JOIN FETCH d.division " +
        "WHERE p.code = :code")
    Optional<PostOffice> findByCodeWithHierarchySafe(@Param("code") String code);
}
