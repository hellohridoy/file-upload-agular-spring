package com.example.File_Image_upload.serviceImpl;

import com.example.File_Image_upload.entity.XlxsFileUploadDownload;
import com.example.File_Image_upload.exceptions.InvalidExcelException;
import com.example.File_Image_upload.repository.XlxsFileUploadDownloadRepository;
import com.example.File_Image_upload.service.XlxsFileUploadDownloadService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class XlxsFileUploadDownloadServiceImpl implements XlxsFileUploadDownloadService {

    private final XlxsFileUploadDownloadRepository repository;
    private static final String[] EXPECTED_HEADERS = {"First Name", "Last Name", "Email", "Phone","Salary"};
    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public String processUpload(MultipartFile file) throws IOException, InvalidExcelException {
        // Read entire file into byte array first
        byte[] fileBytes = file.getBytes();

        // Validate using the byte array
        validateExcelFile(fileBytes, file.getOriginalFilename());

        try (InputStream is = new ByteArrayInputStream(fileBytes);
             Workbook workbook = WorkbookFactory.create(is)) {

            if (!(workbook instanceof XSSFWorkbook)) {
                throw new InvalidExcelException("Only XLSX format supported");
            }

            Sheet sheet = workbook.getSheetAt(0);
            validateHeaderRow(sheet.getRow(0));

            List<XlxsFileUploadDownload> entities = parseDataRows(sheet);
            repository.saveAll(entities);

            return generateDownloadLink();
        } catch (EncryptedDocumentException e) {
            throw new InvalidExcelException("File is password protected");
        }
    }

    private void validateExcelFile(byte[] fileBytes, String filename) throws InvalidExcelException {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new InvalidExcelException("File cannot be empty");
        }

        // Validate file extension
        if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
            throw new InvalidExcelException("Invalid file extension");
        }

        // Validate file signature from byte array
        if (fileBytes.length < 4 ||
            !(fileBytes[0] == 0x50 &&
                fileBytes[1] == 0x4B &&
                fileBytes[2] == 0x03 &&
                fileBytes[3] == 0x04)) {
            throw new InvalidExcelException("Invalid XLSX file signature");
        }
    }
    @Override
    public Workbook generateExcelReport() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employee Data");

        createHeaderRow(sheet);
        populateDataRows(sheet);
        autoSizeColumns(sheet);

        return workbook;
    }

    @Override
    public List<XlxsFileUploadDownload> getAllEntries() {
        return repository.findAll();
    }

    private void validateExcelFile(MultipartFile file) throws InvalidExcelException, IOException {
        if (file == null || file.isEmpty()) {
            throw new InvalidExcelException("File cannot be empty");
        }

        // Verify MIME type
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType())) {
            throw new InvalidExcelException("Invalid file type");
        }

        // Verify file signature without consuming the stream
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int readBytes = is.read(header);

            if (readBytes < 4 ||
                !(header[0] == 0x50 &&  // P
                    header[1] == 0x4B &&  // K
                    header[2] == 0x03 &&
                    header[3] == 0x04)) {
                throw new InvalidExcelException("Invalid XLSX file signature");
            }

            // Reset stream for subsequent processing
            is.reset();
        }
    }
//    private void validateExcelFile(MultipartFile file) throws InvalidExcelException, IOException {
//        // Basic validation
//        if (file == null || file.isEmpty()) {
//            throw new InvalidExcelException("File cannot be empty");
//        }
//
//        // MIME type validation
//        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(file.getContentType())) {
//            throw new InvalidExcelException("Invalid file type. Only XLSX allowed");
//        }
//
//        // File signature validation (PK header for ZIP format)
//        try (InputStream is = file.getInputStream()) {
//            byte[] header = new byte[4];
//            if (is.read(header) != header.length ||
//                !(header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04)) {
//                throw new InvalidExcelException("Invalid file signature - not a valid ZIP/XLSX file");
//            }
//        }
//    }
    private void validateHeaderRow(Row headerRow) throws InvalidExcelException {
        if (headerRow == null) {
            throw new InvalidExcelException("Missing header row");
        }

        for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !EXPECTED_HEADERS[i].equalsIgnoreCase(getCellValue(cell))) {
                throw new InvalidExcelException(
                    String.format("Invalid header format. Expected: %s", String.join(", ", EXPECTED_HEADERS))
                );
            }
        }
    }

    private List<XlxsFileUploadDownload> parseDataRows(Sheet sheet) {
        List<XlxsFileUploadDownload> entities = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            XlxsFileUploadDownload entity = createEntityFromRow(row);
            if (isValidEntity(entity)) {
                entities.add(entity);
            }
        }
        return entities;
    }

    private String generateDownloadLink() {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/excel/download")
            .toUriString();
    }
    private XlxsFileUploadDownload createEntityFromRow(Row row) {
        return XlxsFileUploadDownload.builder()
            .firstName(getCellValue(row.getCell(0)))
            .lastName(getCellValue(row.getCell(1)))
            .email(getCellValue(row.getCell(2)))
            .phone(getCellValue(row.getCell(3)))
            .salary(getCellValue(row.getCell(4)))
            .build();
    }

    private boolean isValidEntity(XlxsFileUploadDownload entity) {
        return entity.getFirstName() != null && !entity.getFirstName().isEmpty() &&
            entity.getLastName() != null && !entity.getLastName().isEmpty() &&
            entity.getEmail() != null && !entity.getEmail().isEmpty();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }


    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
            headerRow.createCell(i).setCellValue(EXPECTED_HEADERS[i]);
        }
    }

    private void populateDataRows(Sheet sheet) {
        List<XlxsFileUploadDownload> data = repository.findAll();
        int rowNum = 1;

        for (XlxsFileUploadDownload item : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getFirstName());
            row.createCell(1).setCellValue(item.getLastName());
            row.createCell(2).setCellValue(item.getEmail());
            row.createCell(3).setCellValue(item.getPhone());
            row.createCell(4).setCellValue(item.getSalary().toString());
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }


}
