package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Facture")
public class Facture {
    @Id
    @Column(name = "id_facture" , length = 20)
    private String idFacture;

    @Column(name = "Date_facturation")
    private LocalDateTime dateFacturation;

    @Column(precision = 12 , scale = 2 , name = "Total_HT")
    private BigDecimal totalHT;

    @Column(precision = 12 , scale = 2 , name = "Total_TVA")
    private BigDecimal totalTVA;

    @Column(precision = 12 , scale = 2 , name = "Total_TTC")
    private BigDecimal totalTTC;

    @Column(name = "Mode_paiement" , length = 100)
    private String modePaiement;

    @Column(name = "Url_facture" , length = 500)
    private String urlFacture;

    @OneToOne
    @JoinColumn(name = "id_commande")
    private Commande commande;
}
