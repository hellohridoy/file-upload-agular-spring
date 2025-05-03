package com.example.File_Image_upload.repository;

import com.example.File_Image_upload.entity.XlxsFileUploadDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XlxsFileUploadDownloadRepository extends JpaRepository<XlxsFileUploadDownload, Long> {
}
