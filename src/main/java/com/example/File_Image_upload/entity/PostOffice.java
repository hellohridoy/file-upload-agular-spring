package com.example.File_Image_upload.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "post_offices")
@Getter
@Setter
public class PostOffice {
    @Id
    @Column(length = 8, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upazila_code")
    private Upazila upazila;
}
