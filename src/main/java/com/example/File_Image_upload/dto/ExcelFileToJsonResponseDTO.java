package com.example.File_Image_upload.dto;

import com.example.File_Image_upload.entity.XlxsFileUploadDownload;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
public class ExcelFileToJsonResponseDTO {
    private String message;
    private String downloadLink;
    private List<XlxsFileUploadDownload> data;

    public static ExcelFileToJsonResponseDTO error(String message) {
        return ExcelFileToJsonResponseDTO.builder().message(message).build();
    }

}
