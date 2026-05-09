package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "LigneCommande")
public class LigneCommande {
    @Id
    @Column(name = "id_ligne_commande" , length = 10)
    private String idLigneCommande;

    @ManyToOne()
    @JoinColumn(name = "commande")
    private Commande commande;

    @ManyToOne()
    @JoinColumn(name = "id_price")
    private Price price;

    @Column(name = "Quantity")
    private int quantity;

    @Column(name = "Historical_price" , precision = 12 , scale = 2)
    private BigDecimal historicalPrice;
}
