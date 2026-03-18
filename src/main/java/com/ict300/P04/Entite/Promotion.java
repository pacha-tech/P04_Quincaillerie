package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "\"Promotion\"")
@Entity
@Data
public class Promotion {
    @Id
    @Column(name = "id_promotion" , length = 10)
    private String idPromotion;

    @ManyToOne
    @JoinColumn(name = "id_price")
    private Price price;

    @Column(precision = 5 , scale = 2 , name = "Taux_remise")
    private BigDecimal tauxRemise;

    @Column(name = "Date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "Date_fin")
    private LocalDateTime dateFin;

    @Column(name = "Est_actif")
    private Boolean estActif;
}
