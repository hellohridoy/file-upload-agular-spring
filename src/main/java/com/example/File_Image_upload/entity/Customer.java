package com.example.File_Image_upload.entity;

import com.example.File_Image_upload.base.uses.AbstractBaseEntity;
import com.example.File_Image_upload.emums.Country;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Getter
@Setter
public class Customer extends AbstractBaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country country;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> additionalInfo;

    @Column(name = "cv_upload_download", columnDefinition = "oid")
    private Long cvUploadDownload;

    private String imageName;
    private String imageType;

    @Column(name = "image_upload_download", columnDefinition = "oid")
    private Long imageUploadDownload;

    private String cvName;
    private String cvType;

    @Lob
    @Column(name = "image_data")
    private byte[] imageData;

    @Lob
    @Column(name = "cv_data")
    private byte[] cvData;

//    @Column(name = "created_at", updatable = false)
//    @CreationTimestamp
//    private LocalDateTime createdAt;

}
