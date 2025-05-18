package com.example.File_Image_upload.base.uses;

import java.util.Date;

public interface AuditableInterface {

    void setCreatedBy(Long createdBy);

    Long getCreatedBy();

    void setCreatedAt(Date createdAt);

    Date getCreatedAt();

    void setUpdatedBy(Long updatedBy);

    Date getUpdatedAt();

    void setVersion(Long version);

    Long getVersion();
}
