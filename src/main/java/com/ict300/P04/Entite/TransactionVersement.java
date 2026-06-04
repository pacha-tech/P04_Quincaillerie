package com.ict300.P04.Entite;

import com.ict300.P04.Utilitaires.StatutPaiement;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "TransactionVersement")
@Data
public class TransactionVersement {
    @Id
    @Column(name = "id_transaction_versement", length = 10)
    private String idTransactionVersement;

    @Column(name = "id_reference", unique = true)
    private String referenceId;

    @Column(name = "id_transaction")
    private String idTransaction;

    @Column(name = "montant_net_transfere")
    private Double montantNetTransfere;

    @Column(name = "numero_telephone")
    private String numeroTelephone;

    @Column(name = "operateur")
    private String operateur;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    private StatutPaiement statut;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_mise_a_jour")
    private LocalDateTime dateMiseAJour;
}
