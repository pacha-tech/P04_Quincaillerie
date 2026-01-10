package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"Quincaillerie\"")
public class Quincaillerie {
    @Id
    @Column(name = "id_quincaillerie")
    private String idQuincaillerie;

    @OneToOne
    @JoinColumn(name = "id_user")
    private User admin;

    @Column(name = "Store_name")
    private String storeName;

    @Column(name = "Quartier")
    private String quartier;

    @Column(name = "City")
    private String city;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Address")
    private String adress;

    @Column(name = "Notice_count")
    private int noticeCount;

    @Column(name = "Creation_date")
    private LocalDateTime creationDate;

    @Column(precision = 10 , scale = 8 , name = "Latitude")
    private BigDecimal latitude;

    @Column(precision = 11 , scale = 8 , name = "Longitude")
    private BigDecimal longitude;

    @Column(name = "Photo_url")
    private String photoUrl;

    @Column(name = "type_structure")
    private String typeStructure;

    @Column(precision = 3 , scale = 2 , name = "Average_rating")
    private BigDecimal averageRating;

    @Column(name = "Statut")
    private String status;

    @OneToMany(mappedBy = "quincaillerie")
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "quincaillerie")
    private List<Price> prices = new ArrayList<>();

    @OneToMany(mappedBy = "quincaillerie")
    private List<FavoriteQuincaillerie> favoriteQuincailleries = new ArrayList<>();
}
