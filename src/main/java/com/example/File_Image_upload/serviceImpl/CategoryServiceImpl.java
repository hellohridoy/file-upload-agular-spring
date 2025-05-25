package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.CategoryCreateRequestDto;
import com.example.File_Image_upload.dto.CategoryResponseDto;
import com.example.File_Image_upload.entity.Categories;
import com.example.File_Image_upload.repository.CategoryRepository;
import com.example.File_Image_upload.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto createCategory(CategoryCreateRequestDto dto) {
        Categories category = new Categories();
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        if (dto.getParentId() != null) {
            categoryRepository.findById(dto.getParentId()).ifPresent(category::setParentCategory);
        }

        Categories saved = categoryRepository.save(category);
        return convertToDto(saved);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryCreateRequestDto dto) {
        Categories category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());

        if (dto.getParentId() != null) {
            categoryRepository.findById(dto.getParentId()).ifPresent(category::setParentCategory);
        } else {
            category.setParentCategory(null);
        }

        return convertToDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto convertToDto(Categories category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setParentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null);

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            dto.setSubCategories(category.getSubCategories()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList()));
        }

        return dto;
    }
}

