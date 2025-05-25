package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Categories, Long> {
    // Find category by name (case-sensitive)
    Optional<Categories> findByName(String name);

    // Find category by name (case-insensitive)
    @Query("SELECT c FROM Categories c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Categories> findByNameIgnoreCase(@Param("name") String name);
//
//    // Check if category exists by name
//    boolean existsByName(String name);
//
//    // Check if category exists by name (case-insensitive)
//    @Query("SELECT COUNT(c) > 0 FROM Categories c WHERE LOWER(c.name) = LOWER(:name)")
//    boolean existsByNameIgnoreCase(@Param("name") String name);
//
//    // Find categories containing name (search functionality)
//    @Query("SELECT c FROM Categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
//    List<Categories> findByNameContainingIgnoreCase(@Param("name") String name);
//
//    // Find all categories ordered by name
//    @Query("SELECT c FROM Categories c ORDER BY c.name ASC")
//    List<Categories> findAllOrderByName();
//
//    // Find categories with description containing keyword
//    @Query("SELECT c FROM Categories c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<Categories> findByDescriptionContainingIgnoreCase(@Param("keyword") String keyword);
//
//    // Find categories by name or description containing keyword
//    @Query("SELECT c FROM Categories c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
//    List<Categories> searchByKeyword(@Param("keyword") String keyword);

//    // Count products in each category
//    @Query("SELECT c.name, COUNT(p) FROM Categories c LEFT JOIN c.products p GROUP BY c.id, c.name")
//    List<Object[]> countProductsByCategory();
//
//    // Find categories with products
//    @Query("SELECT DISTINCT c FROM Categories c WHERE SIZE(c.products) > 0")
//    List<Categories> findCategoriesWithProducts();
//
//    // Find categories without products
//    @Query("SELECT c FROM Categories c WHERE SIZE(c.products) = 0")
//    List<Categories> findCategoriesWithoutProducts();


}
