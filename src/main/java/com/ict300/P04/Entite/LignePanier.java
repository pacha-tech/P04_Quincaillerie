package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "LignePanier")
public class LignePanier {
    @Id
    @Column(name = "id_ligne_panier" , length = 10)
    private String idLignePanier;

    @ManyToOne()
    @JoinColumn(name = "panier")
    private Panier panier;

    @ManyToOne()
    @JoinColumn(name = "id_price")
    private Price price;

    @Column(name = "Quantity")
    private int quantity;
}
