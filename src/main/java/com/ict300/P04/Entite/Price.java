package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"Price\"")
public class Price {
    @Id
    @Column(name = "id_price" , length = 10)
    private String idPrice;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_quincaillerie")
    private Quincaillerie quincaillerie;

    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name = "Published_by_user")
    private User user;

    @Column(precision = 12 , scale = 2 , name = "Price")
    private BigDecimal price;

    @Column(precision = 12 , scale = 2 , name = "Purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "Stock")
    private int stock;

    @OneToMany(mappedBy = "price")
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "price")
    private List<Vente> ventes = new ArrayList<>();

    @OneToMany(mappedBy = "price")
    private List<Stock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "price")
    private List<Promotion> promotions = new ArrayList<>();

    @OneToMany(mappedBy = "price")
    private List<Panier> paniers = new ArrayList<>();
}
