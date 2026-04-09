package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "User")
public class User {
    @Id
    @Column(name = "id_user" , length = 128)
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

    @Column(name = "Role")
    private String role;

    @Column(name = "Photo_url")
    private String photoUrl;

    @Column(name = "Status")
    private String status;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_online", columnDefinition = "ENUM('ON_LINE', 'OFF_LINE') DEFAULT 'OFF_LINE'")
    private UserStatus statut_online = UserStatus.OFF_LINE;

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

    @OneToMany(mappedBy = "user")
    private List<Facture> factures = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Panier> paniers = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Conversation> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "recipient")
    private List<Notification> notifications = new ArrayList<>();

    public enum UserStatus {
        ON_LINE,
        OFF_LINE
    }
}
