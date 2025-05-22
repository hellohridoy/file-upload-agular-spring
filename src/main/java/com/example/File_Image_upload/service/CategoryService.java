package com.example.File_Image_upload.service;

import com.example.File_Image_upload.dto.CategoryCreateRequestDto;
import com.example.File_Image_upload.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryCreateRequestDto dto);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategoryById(Long id);

    CategoryResponseDto updateCategory(Long id, CategoryCreateRequestDto dto);

    void deleteCategory(Long id);
}
