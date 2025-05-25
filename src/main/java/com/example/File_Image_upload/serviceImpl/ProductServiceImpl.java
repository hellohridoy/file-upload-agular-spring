package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.dto.ProductCreateRequestDto;
import com.example.File_Image_upload.dto.ProductResponseDto;
import com.example.File_Image_upload.dto.ProductUploadError;
import com.example.File_Image_upload.dto.ProductUploadResponseDto;
import com.example.File_Image_upload.entity.Categories;
import com.example.File_Image_upload.entity.Product;
import com.example.File_Image_upload.repository.CategoryRepository;
import com.example.File_Image_upload.repository.ProductRepository;
import com.example.File_Image_upload.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    // Expected column headers in Excel file (flexible mapping)
    private static final String[] EXPECTED_HEADERS = {
        "name", "description", "price", "sku", "quantity", "imageUrl", "categoryName"
    };

    private final ProductRepository productRepository;
    private final CategoryRepository categoriesRepository;

    @Override
    public ProductResponseDto createProduct(ProductCreateRequestDto request) {
        Categories category = categoriesRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return convertToDto(product);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll()
            .stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductCreateRequestDto request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Categories category = categoriesRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setSku(request.getSku());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);
        return convertToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

//    @Override
//    public ProductResponseDto createProduct(ProductCreateRequestDto request) {
//        Categories category = categoriesRepository.findById(request.getCategoryId())
//            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//
//        Product product = new Product();
//        product.setName(request.getName());
//        product.setDescription(request.getDescription());
//        product.setPrice(request.getPrice());
//        product.setSku(request.getSku());
//        product.setQuantity(request.getQuantity());
//        product.setImageUrl(request.getImageUrl());
//        product.setCategory(category);
//
//        Product savedProduct = productRepository.save(product);
//        return convertToDto(savedProduct);
//    }
//
//    @Override
//    public ProductResponseDto getProductById(Long id) {
//        Product product = productRepository.findById(id)
//            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
//        return convertToDto(product);
//    }
//
//    @Override
//    public List<ProductResponseDto> getAllProducts() {
//        return productRepository.findAll()
//            .stream()
//            .map(this::convertToDto)
//            .collect(Collectors.toList());
//    }
//
//    @Override
//    public ProductResponseDto updateProduct(Long id, ProductCreateRequestDto request) {
//        Product product = productRepository.findById(id)
//            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
//
//        Categories category = categoriesRepository.findById(request.getCategoryId())
//            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//
//        product.setName(request.getName());
//        product.setDescription(request.getDescription());
//        product.setPrice(request.getPrice());
//        product.setSku(request.getSku());
//        product.setQuantity(request.getQuantity());
//        product.setImageUrl(request.getImageUrl());
//        product.setCategory(category);
//
//        Product updatedProduct = productRepository.save(product);
//        return convertToDto(updatedProduct);
//    }

//    @Override
//    public void deleteProduct(Long id) {
//        if (!productRepository.existsById(id)) {
//            throw new EntityNotFoundException("Product not found");
//        }
//        productRepository.deleteById(id);
//    }

    @Override
    @Transactional
    public ProductUploadResponseDto uploadProductsFromExcel(MultipartFile file) {
        List<ProductUploadError> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try {
            // Validate file
            if (file.isEmpty()) {
                return new ProductUploadResponseDto(false, "File is empty");
            }

            if (!isExcelFile(file)) {
                return new ProductUploadResponseDto(false, "Please upload a valid Excel file (.xlsx or .xls)");
            }

            // Read and process Excel file
            List<ProductCreateRequestDto> productRequests = readProductsFromExcel(file, errors, warnings);

            if (!errors.isEmpty() && productRequests.isEmpty()) {
                ProductUploadResponseDto response = new ProductUploadResponseDto(false, "Failed to read Excel file");
                response.setErrors(errors);
                return response;
            }

            // Process each product using existing ProductService
            int successCount = 0;
            int failCount = 0;

            for (int i = 0; i < productRequests.size(); i++) {
                ProductCreateRequestDto request = productRequests.get(i);
                int rowNumber = i + 2; // +2 because Excel starts from 1 and we skip header

                try {
                    // Use existing createProduct method
                    createProduct(request);
                    successCount++;
                    logger.info("Successfully created product: {} (Row {})", request.getName(), rowNumber);

                } catch (Exception e) {
                    logger.error("Error creating product at row {}: ", rowNumber, e);
                    errors.add(new ProductUploadError(rowNumber, "Creation",
                        "Error creating product: " + e.getMessage(), request.getName()));
                    failCount++;
                }
            }

            // Prepare response
            ProductUploadResponseDto response = new ProductUploadResponseDto();
            response.setSuccess(successCount > 0);
            response.setTotalRows(productRequests.size());
            response.setSuccessfullyProcessed(successCount);
            response.setFailed(failCount);
            response.setErrors(errors);
            response.setWarnings(warnings);

            if (successCount > 0 && failCount == 0) {
                response.setMessage(String.format("All %d products uploaded successfully", successCount));
            } else if (successCount > 0 && failCount > 0) {
                response.setMessage(String.format("Partially successful: %d uploaded, %d failed", successCount, failCount));
            } else {
                response.setMessage("Upload failed");
            }

            return response;

        } catch (Exception e) {
            logger.error("Error during product upload: ", e);
            ProductUploadResponseDto response = new ProductUploadResponseDto(false, "Upload failed: " + e.getMessage());
            response.setErrors(errors);
            return response;
        }
    }

    private List<ProductCreateRequestDto> readProductsFromExcel(MultipartFile file,
                                                                List<ProductUploadError> errors,
                                                                List<String> warnings) throws IOException {

        List<ProductCreateRequestDto> products = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Validate header row
            if (!rowIterator.hasNext()) {
                errors.add(new ProductUploadError(0, "File", "Excel file is empty", ""));
                return products;
            }

            Row headerRow = rowIterator.next();
            if (!validateHeaders(headerRow, errors)) {
                return products;
            }

            int rowNumber = 1; // Start from 1 (after header)

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowNumber++;

                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }

                ProductCreateRequestDto product = parseProductFromRow(row, rowNumber, errors, warnings);
                if (product != null) {
                    products.add(product);
                }
            }

        } catch (Exception e) {
            logger.error("Error reading Excel file: ", e);
            errors.add(new ProductUploadError(0, "File", "Error reading Excel file: " + e.getMessage(), ""));
        }

        return products;
    }

    private boolean validateHeaders(Row headerRow, List<ProductUploadError> errors) {
        // Define acceptable header variations for each column
        String[][] acceptableHeaders = {
            // Column 0 - Product Name
            {"name", "product name", "productname", "item name", "title"},

            // Column 1 - Description
            {"description", "desc", "product description", "details", "summary"},

            // Column 2 - Price
            {"price", "product price", "cost", "amount", "value"},

            // Column 3 - SKU
            {"sku", "product code", "item code", "code", "barcode", "id"},

            // Column 4 - Quantity
            {"quantity", "qty", "stock", "stock quantity", "inventory", "count"},

            // Column 5 - Image URL
            {"imageurl", "image url", "image", "picture", "photo", "pic", "img"},

            // Column 6 - Category
            {"categoryname", "category name", "category", "cat", "type", "group", "category id", "categoryid"}
        };

        // Check minimum required columns
        int minColumns = acceptableHeaders.length;
        if (headerRow.getLastCellNum() < minColumns) {
            errors.add(new ProductUploadError(1, "Header",
                "Not enough columns. Expected " + minColumns + " columns, found " + headerRow.getLastCellNum(), ""));
            return false;
        }

        // Check each column
        for (int i = 0; i < acceptableHeaders.length; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) {
                errors.add(new ProductUploadError(1, "Header",
                    "Missing header at column " + (i + 1), "null"));
                return false;
            }

            String actualHeader = getCellValueAsString(cell).trim().toLowerCase();
            boolean found = false;

            // Check if actual header matches any acceptable variation
            for (String acceptable : acceptableHeaders[i]) {
                if (acceptable.equals(actualHeader)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                String expectedOptions = String.join(", ", acceptableHeaders[i]);
                errors.add(new ProductUploadError(1, "Header",
                    "Invalid header at column " + (i + 1) + ". Expected one of: [" + expectedOptions + "]",
                    getCellValueAsString(cell)));
                return false;
            }
        }

        return true;
    }

    private ProductCreateRequestDto parseProductFromRow(Row row, int rowNumber,
                                                        List<ProductUploadError> errors,
                                                        List<String> warnings) {
        ProductCreateRequestDto product = new ProductCreateRequestDto();
        boolean hasErrors = false;

        try {
            // Name (Required) - Column 0
            String name = getCellValueAsString(row.getCell(0));
            if (name.trim().isEmpty()) {
                errors.add(new ProductUploadError(rowNumber, "name", "Product name is required", name));
                hasErrors = true;
            } else {
                product.setName(name.trim());
            }

            // Description - Column 1
            product.setDescription(getCellValueAsString(row.getCell(1)).trim());

            // Price (Required) - Column 2
            try {
                BigDecimal price = getCellValueAsBigDecimal(row.getCell(2));
                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add(new ProductUploadError(rowNumber, "price", "Price must be a positive number",
                        price != null ? price.toString() : "null"));
                    hasErrors = true;
                } else {
                    product.setPrice(price);
                }
            } catch (Exception e) {
                errors.add(new ProductUploadError(rowNumber, "price", "Invalid price format",
                    getCellValueAsString(row.getCell(2))));
                hasErrors = true;
            }

            // SKU (Required) - Column 3
            String sku = getCellValueAsString(row.getCell(3));
            if (sku.trim().isEmpty()) {
                errors.add(new ProductUploadError(rowNumber, "sku", "SKU is required", sku));
                hasErrors = true;
            } else if (productRepository.existsBySku(sku.trim())) {
                errors.add(new ProductUploadError(rowNumber, "sku", "SKU already exists", sku.trim()));
                hasErrors = true;
            } else {
                product.setSku(sku.trim());
            }

            // Quantity - Column 4
            try {
                Integer quantity = getCellValueAsInteger(row.getCell(4));
                product.setQuantity(quantity != null ? quantity : 0);
            } catch (Exception e) {
                errors.add(new ProductUploadError(rowNumber, "quantity", "Invalid quantity format",
                    getCellValueAsString(row.getCell(4))));
                product.setQuantity(0); // Default value
            }

            // Image URL - Column 5
            product.setImageUrl(getCellValueAsString(row.getCell(5)).trim());

            // Category Name/ID -> Category ID (Required) - Column 6
            String categoryValue = getCellValueAsString(row.getCell(6));
            if (categoryValue.trim().isEmpty()) {
                errors.add(new ProductUploadError(rowNumber, "category", "Category is required", categoryValue));
                hasErrors = true;
            } else {
                Long categoryId = null;

                // Check if it's a numeric ID
                try {
                    Long directId = Long.parseLong(categoryValue.trim());
                    // Verify the category exists
                    if (categoriesRepository.findById(directId).isPresent()) {
                        categoryId = directId;
                    } else {
                        errors.add(new ProductUploadError(rowNumber, "category",
                            "Category ID " + directId + " not found", categoryValue));
                        hasErrors = true;
                    }
                } catch (NumberFormatException e) {
                    // It's a category name, find or create it
                    categoryId = findOrCreateCategory(categoryValue.trim(), rowNumber, warnings);
                    if (categoryId == null) {
                        errors.add(new ProductUploadError(rowNumber, "category",
                            "Could not find or create category", categoryValue));
                        hasErrors = true;
                    }
                }

                if (categoryId != null) {
                    product.setCategoryId(categoryId);
                }
            }

        } catch (Exception e) {
            logger.error("Error parsing row {}: ", rowNumber, e);
            errors.add(new ProductUploadError(rowNumber, "General", "Error parsing row: " + e.getMessage(), ""));
            hasErrors = true;
        }

        return hasErrors ? null : product;
    }

    private Long findOrCreateCategory(String categoryName, int rowNumber, List<String> warnings) {
        try {
            // Try case-insensitive search first
            Optional<Categories> existingCategory = categoriesRepository.findByNameIgnoreCase(categoryName);

            if (existingCategory.isPresent()) {
                return existingCategory.get().getId();
            }

            // Fallback to exact match
            existingCategory = categoriesRepository.findByName(categoryName);
            if (existingCategory.isPresent()) {
                return existingCategory.get().getId();
            }

            // Create new category
            Categories newCategory = new Categories();
            newCategory.setName(categoryName);
            newCategory.setDescription("Auto-created during product upload");

            Categories savedCategory = categoriesRepository.save(newCategory);
            warnings.add(String.format("Row %d: Created new category '%s'", rowNumber, categoryName));

            return savedCategory.getId();

        } catch (Exception e) {
            logger.error("Error finding/creating category '{}': ", categoryName, e);
            return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        // Check first 3 required columns (name, description, price)
        for (int i = 0; i < 3; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    // Try to get the cached formula result
                    switch (cell.getCachedFormulaResultType()) {
                        case NUMERIC:
                            double numericValue = cell.getNumericCellValue();
                            if (numericValue == (long) numericValue) {
                                return String.valueOf((long) numericValue);
                            } else {
                                return String.valueOf(numericValue);
                            }
                        case STRING:
                            return cell.getStringCellValue();
                        default:
                            return cell.getCellFormula();
                    }
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            default:
                return "";
        }
    }

    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                String value = cell.getStringCellValue().trim();
                if (value.isEmpty()) return null;
                try {
                    // Remove common currency symbols and formatting
                    value = value.replaceAll("[^0-9.-]", "");
                    return new BigDecimal(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            case FORMULA:
                try {
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                String value = cell.getStringCellValue().trim();
                if (value.isEmpty()) return null;
                try {
                    return Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            case FORMULA:
                try {
                    return (int) cell.getNumericCellValue();
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private boolean isExcelFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null && (fileName.endsWith(".xlsx") || fileName.endsWith(".xls"));
    }

    @Override
    public String getExcelTemplate() {
        return """
            Excel Upload Template Instructions:

            Required Columns (flexible headers accepted):

            Column 1 - Product Name:
            ✓ Accepted: name, Product Name, productname, Item Name, title

            Column 2 - Description:
            ✓ Accepted: description, desc, Product Description, details, summary

            Column 3 - Price:
            ✓ Accepted: price, Product Price, cost, amount, value

            Column 4 - SKU:
            ✓ Accepted: sku, Product Code, Item Code, code, barcode, id

            Column 5 - Quantity:
            ✓ Accepted: quantity, qty, stock, Stock Quantity, inventory, count

            Column 6 - Image URL:
            ✓ Accepted: imageUrl, Image URL, image, picture, photo, pic, img

            Column 7 - Category:
            ✓ Accepted: categoryName, Category Name, category, cat, type, group, Category ID, categoryId
            ✓ Can use either Category Name (will auto-create) or Category ID (existing categories)

            Example Excel Content (Option 1 - Simple):
            | name          | description           | price  | sku     | quantity | imageUrl                     | categoryName |
            |---------------|----------------------|--------|---------|----------|------------------------------|--------------|
            | iPhone 14     | Latest iPhone model  | 999.99 | IP14    | 10       | http://example.com/ip14.jpg  | Electronics  |
            | Samsung S23   | Android smartphone   | 899.99 | SS23    | 5        | http://example.com/ss23.jpg  | Electronics  |

            Example Excel Content (Option 2 - User-Friendly):
            | Product Name  | Description           | Price  | SKU     | Quantity | Image URL                    | Category Name |
            |---------------|----------------------|--------|---------|----------|------------------------------|---------------|
            | iPhone 14     | Latest iPhone model  | 999.99 | IP14    | 10       | http://example.com/ip14.jpg  | Electronics   |
            | Samsung S23   | Android smartphone   | 899.99 | SS23    | 5        | http://example.com/ss23.jpg  | Electronics   |

            Example Excel Content (Option 3 - Using Category ID):
            | Product Name  | Description           | Price  | SKU     | Quantity | Image URL                    | Category ID |
            |---------------|----------------------|--------|---------|----------|------------------------------|-------------|
            | iPhone 14     | Latest iPhone model  | 999.99 | IP14    | 10       | http://example.com/ip14.jpg  | 1           |
            | Samsung S23   | Android smartphone   | 899.99 | SS23    | 5        | http://example.com/ss23.jpg  | 1           |
            - Headers are case-insensitive and flexible (both "imageUrl" and "Image URL" work)
            - SKU must be unique across all products
            - Price must be a positive number (currency symbols will be removed automatically)
            - Categories will be auto-created if they don't exist
            - File must be in .xlsx or .xls format
            - Empty rows will be skipped
            - Required fields: Product Name, Price, SKU, Category
            """;
    }

    private ProductResponseDto convertToDto(Product product) {
        ProductResponseDto dto = new ProductResponseDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSku(product.getSku());
        dto.setQuantity(product.getQuantity());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }
}
