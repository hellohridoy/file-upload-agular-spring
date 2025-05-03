package com.example.File_Image_upload.controller;

import com.example.File_Image_upload.dto.ExcelFileToJsonResponseDTO;
import com.example.File_Image_upload.entity.XlxsFileUploadDownload;
import com.example.File_Image_upload.exceptions.InvalidExcelException;
import com.example.File_Image_upload.service.XlxsFileUploadDownloadService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExcelController {

    private final XlxsFileUploadDownloadService excelService;

    @PostMapping("/api/excel/excel-upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            String downloadLink = excelService.processUpload(file);
            return ResponseEntity.ok().body("File processed successfully. Download link: " + downloadLink);
        } catch (InvalidExcelException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }


    // Upload endpoint with JSON response
    @PostMapping("/api/excel/excel-upload/json-response")
    public ResponseEntity<ExcelFileToJsonResponseDTO> handleFileUploadToGetJson(@RequestParam("file") MultipartFile file) {
        try {
            String downloadLink = excelService.processUpload(file);
            return ResponseEntity.ok(ExcelFileToJsonResponseDTO.builder()
                .message("File processed successfully")
                .downloadLink(downloadLink)
                .build());
        } catch (InvalidExcelException e) {
            return ResponseEntity.badRequest()
                .body(ExcelFileToJsonResponseDTO.error(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ExcelFileToJsonResponseDTO.error("Processing error: " + e.getMessage()));
        }
    }

    @GetMapping("/api/excel/excel-upload/data")
    public ResponseEntity<ExcelFileToJsonResponseDTO> getAllData() {
        List<XlxsFileUploadDownload> data = excelService.getAllEntries();
        return ResponseEntity.ok(ExcelFileToJsonResponseDTO.builder()
            .message("Data retrieved successfully")
            .data(data)
            .build());
    }

    @GetMapping("/download")
    public void handleFileDownload(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=employee_data.xlsx");

            Workbook workbook = excelService.generateExcelReport();
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
