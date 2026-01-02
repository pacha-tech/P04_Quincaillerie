package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"Category\"")
public class Category {
    @Id
    @Column(name = "id_category")
    private String idCategory;

    @Column(name = "Description")
    private String description;

    @Column(name = "Name")
    private String name;

    @Column(name = "Image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

}
