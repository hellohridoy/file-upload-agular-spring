package com.example.File_Image_upload.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "upazilas")
@Getter
@Setter
public class Upazila {
    @Id
    @Column(length = 6, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_code")
    private District district;

    @OneToMany(mappedBy = "upazila", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostOffice> postOffices = new ArrayList<>();
}
