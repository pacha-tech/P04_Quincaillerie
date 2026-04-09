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
    @Column(name = "id_facture" , length = 10)
    private String idFacture;

    @ManyToOne
    @JoinColumn(name = "id_quincaillerie")
    private Quincaillerie quincaillerie;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "Date_facture")
    private LocalDateTime dateFacture;

    @Column(precision = 12 , scale = 2 , name = "Total_HT")
    private BigDecimal totalHT;

    @Column(precision = 12 , scale = 2 , name = "Total_TVA")
    private BigDecimal totalTVA;

    @Column(precision = 12 , scale = 2 , name = "Total_TTC")
    private BigDecimal totalTTC;

    @OneToMany(mappedBy = "facture")
    private List<Vente> ventes = new ArrayList<>();
}
