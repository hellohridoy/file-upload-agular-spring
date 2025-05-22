package com.example.File_Image_upload.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "districts")
@Getter
@Setter
public class District {
    @Id
    @Column(length = 4, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_code")
    private Division division;  // This property must exist for the mapping

    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Upazila> upazilas = new ArrayList<>();
}
