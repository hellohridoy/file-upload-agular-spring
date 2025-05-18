package com.example.File_Image_upload.base.uses;

import java.util.Date;

public interface SoftDeletableInterface {

    void setDeletedBy(Long deletedBy);

    Long getDeletedBy();

    void setDeletedAt(Date deletedAt);

    Date getDeletedAt();

    void setDeleted(Boolean deleted);

    Boolean getDeleted();
}
