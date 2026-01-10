package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"User\"")
public class User {
    @Id
    @Column(name = "id_user")
    private String idUser;

    @Column(name = "Name")
    private String name;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Email")
    private String email;

    @Column(name = "Registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "Last_login")
    private LocalDateTime lastLogin;

    @Column(name = "Password")
    private String password;

    @Column(name = "Role")
    private String role;

    @Column(name = "Photo_url")
    private String photoUrl;

    @Column(name = "Status")
    private String status;

    @OneToOne(mappedBy = "admin")
    private Quincaillerie quincaillerie;

    @OneToMany(mappedBy = "user")
    private List<Notice> notices = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Price> prices = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<FavoriteProduct> favoriteProducts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<FavoriteQuincaillerie> favoriteQuincailleries = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<HistoricalSearch> historicalSearchs = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Report> reports = new ArrayList<>();
}
