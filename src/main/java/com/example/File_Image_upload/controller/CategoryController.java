package com.example.File_Image_upload.controller;
import com.example.File_Image_upload.dto.CategoryCreateRequestDto;
import com.example.File_Image_upload.dto.CategoryResponseDto;
import com.example.File_Image_upload.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/api/v1/category/category-infos")
    public CategoryResponseDto create(@RequestBody CategoryCreateRequestDto dto) {
        return categoryService.createCategory(dto);
    }

    @GetMapping("/api/v1/category/category-infos")
    public List<CategoryResponseDto> getAll() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/api/v1/category/category-infos/{id}")
    public CategoryResponseDto getById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }



    @PutMapping("/api/v1/category/category-infos/{id}")
    public CategoryResponseDto update(@PathVariable Long id, @RequestBody CategoryCreateRequestDto dto) {
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/api/v1/category/category-infos/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
