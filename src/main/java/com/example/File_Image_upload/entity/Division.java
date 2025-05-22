package com.example.File_Image_upload.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "divisions")
@Getter
@Setter
public class Division {
    @Id
    @Column(length = 2, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "division", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<District> districts = new ArrayList<>();
}
