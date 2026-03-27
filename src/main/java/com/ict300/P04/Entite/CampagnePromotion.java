package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "\"CampagnePromotion\"")
public class CampagnePromotion {
    @Id
    @Column(name = "id_campagnePromotion" , length = 10)
    private String idCampagnePromotion;

    @Column(precision = 5 , scale = 2 , name = "Taux_remise")
    private BigDecimal tauxRemise;

    @Column(name = "Date_debut" , columnDefinition = "DATETIME")
    private LocalDate dateDebut;

    @Column(name = "Date_fin" , columnDefinition = "DATETIME")
    private LocalDate dateFin;

    @Column(name = "Est_actif")
    private Boolean estActif;

    @ManyToOne
    @JoinColumn(name = "id_quincaillerie")
    private Quincaillerie quincaillerie;

    @OneToMany(mappedBy = "campagnePromotion")
    private List<Promotion> promotions = new ArrayList<>();

    @Column(name = "name")
    private String nom;
}
