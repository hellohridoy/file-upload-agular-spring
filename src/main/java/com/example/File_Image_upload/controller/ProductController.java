package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.dto.ProductCreateRequestDto;
import com.example.File_Image_upload.dto.ProductResponseDto;
import com.example.File_Image_upload.dto.ProductUploadResponseDto;
import com.example.File_Image_upload.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/api/v1/products/product-infos")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductCreateRequestDto request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/api/v1/products/product-infos/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/api/v1/products/product-infos")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/api/v1/products/product-infos/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                            @RequestBody ProductCreateRequestDto request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/api/v1/products/product-infos/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/api/v1/products/product-infos/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductUploadResponseDto> uploadProductsFromExcel(
        @RequestParam("file") MultipartFile file) {

        ProductUploadResponseDto response = productService.uploadProductsFromExcel(file);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/v1/products/product-infos/excel-template")
    public ResponseEntity<String> getExcelTemplate() {
        String template = productService.getExcelTemplate();
        return ResponseEntity.ok(template);
    }
}
