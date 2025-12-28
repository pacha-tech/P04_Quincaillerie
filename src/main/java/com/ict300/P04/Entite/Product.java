package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"Product\"")
public class Product {
    @Id
    @Column(name = "id_product")
    private String idProduct;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;

    @Column(name = "Description")
    private String description;

    @Column(name = "Name")
    private String name;

    @Column(name = "Brand")
    private String brand;

    @Column(name = "Unit")
    private String unit;

    @Column(name = "Image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "product")
    private List<Price> prices = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<FavoriteProduct> favoriteProducts = new ArrayList<>();
}
