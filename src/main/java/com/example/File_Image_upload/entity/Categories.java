package com.example.File_Image_upload.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String slug; // URL friendly version of name

    private String description;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Categories parentCategory;

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Categories> subCategories;

    // Constructors
    public Categories() {}

    public Categories(String name, String slug, String description, String imageUrl, Categories parentCategory) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.imageUrl = imageUrl;
        this.parentCategory = parentCategory;
    }

}
