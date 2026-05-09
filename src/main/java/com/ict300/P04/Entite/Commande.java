package com.ict300.P04.Entite;

import com.ict300.P04.Utilitaires.StatutCommande;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "Commande")
@Entity
@Data
public class Commande {
    @Id
    @Column(name = "id_commande", length = 20)
    private String idCommande;

    @Enumerated(EnumType.STRING)
    @Column(name = "Statut")
    private StatutCommande statut;

    @Column(name = "Montant_total" , precision = 12 , scale = 2)
    private BigDecimal montantTotal;

    @ManyToOne()
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne()
    @JoinColumn(name = "id_quincaillerie")
    private Quincaillerie quincaillerie;

    @OneToOne(mappedBy = "commande")
    private Facture facture;

    @OneToOne(mappedBy = "commande")
    private DetailRetrait detailRetrait;

    @OneToOne(mappedBy = "commande")
    private RetraitCode retraitCode;

    @OneToOne(mappedBy = "commande")
    private DetailCommande detailCommande;

    @OneToMany(mappedBy = "commande")
    private List<LigneCommande> ligneCommandes = new ArrayList<>();
}
