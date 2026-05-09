package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "DetailCommande")
public class DetailCommande {
    @Id
    @Column(name = "id_detail_commande", length = 10)
    private String idDetailCommande;

    @Column(name = "Date_commande")
    private LocalDateTime dateCommande;

    @Column(name = "Date_annulation")
    private LocalDateTime dateAnnulation;

    @Column(name = "Date_validation")
    private LocalDateTime dateValidation;

    @Column(name = "Date_paiement")
    private LocalDateTime datePaiement;

    @Column(name = "Date_Retrait")
    private LocalDateTime dateRetrait;

    @OneToOne
    @JoinColumn(name = "id_commande")
    private Commande commande;
}
