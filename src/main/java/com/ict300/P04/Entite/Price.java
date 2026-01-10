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
    @Column(name = "id_price")
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

    @Column(name = "Stock")
    private String stock;

    @Column(name = "Promotion_rating")
    private String promotionRating;

    @OneToMany(mappedBy = "price")
    private List<Report> reports = new ArrayList<>();
}
