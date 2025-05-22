package com.example.File_Image_upload.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Long parentId;
    private List<CategoryResponseDto> subCategories; // recursive list
}
