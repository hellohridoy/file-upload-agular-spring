package com.example.File_Image_upload.service;

import com.example.File_Image_upload.dto.ProductCreateRequestDto;
import com.example.File_Image_upload.dto.ProductResponseDto;
import com.example.File_Image_upload.dto.ProductUploadResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductCreateRequestDto request);

    ProductResponseDto getProductById(Long id);

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto updateProduct(Long id, ProductCreateRequestDto request);

    void deleteProduct(Long id);

    ProductUploadResponseDto uploadProductsFromExcel(MultipartFile file);
    String getExcelTemplate();

}
