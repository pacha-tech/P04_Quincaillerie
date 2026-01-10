package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "\"Favorite_product\"")
public class FavoriteProduct {
    @Id
    @Column(name = "id_favorite_product" , length = 10)
    private String idFavoriteProduct;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @Column(name = "Date_added")
    private LocalDateTime dateAdded;

    @Column(precision = 12 , scale = 2 , name = "Target_price")
    private BigDecimal targetPrice;
}
