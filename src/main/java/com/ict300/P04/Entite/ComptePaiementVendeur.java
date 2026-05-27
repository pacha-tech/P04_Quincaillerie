package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ComptePaiementVendeur")
public class ComptePaiementVendeur {
    @Id
    @Column(name = "id_compte_paiement_vendeur")
    private Long idComptePaiementVendeur;

    @OneToOne
    @JoinColumn(name = "vendeur_id", nullable = false, unique = true)
    private Quincaillerie quincaillerie;

    @Column( name = "nom_compte", nullable = false)
    private String nomCompte;

    @Column(name = "num_telephone", nullable = false, length = 15)
    private String numeroTelephone;


    private String operateur;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "last_update")
    private LocalDateTime dateDerniereModification;
}
