package com.example.File_Image_upload.service;

import com.example.File_Image_upload.entity.XlxsFileUploadDownload;
import com.example.File_Image_upload.exceptions.InvalidExcelException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface XlxsFileUploadDownloadService {

    String processUpload(MultipartFile file) throws IOException, InvalidExcelException;
    Workbook generateExcelReport() throws IOException;
    List<XlxsFileUploadDownload> getAllEntries(); // New method

}
