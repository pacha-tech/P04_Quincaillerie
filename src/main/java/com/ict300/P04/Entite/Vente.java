package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Table(name = "Vente")
@Entity
@Data
public class Vente {
    @Id
    @Column(name = "id_vente" , length = 10)
    private String idVente;

    @ManyToOne
    @JoinColumn(name = "id_facture")
    private Facture facture;

    @ManyToOne
    @JoinColumn(name = "id_price")
    private Price price;

    @Column(name = "Quantity")
    private int quantity;

    @Column(precision = 12 , scale = 2 , name = "Unit_price")
    private BigDecimal unitPrice;

    @Column(precision = 12 , scale = 2 , name = "Total")
    private BigDecimal total;
}
