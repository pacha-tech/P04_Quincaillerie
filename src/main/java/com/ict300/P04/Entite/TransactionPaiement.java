package com.ict300.P04.Entite;

import com.ict300.P04.Utilitaires.StatutPaiement;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "TransactionPaiement")
@Data
public class TransactionPaiement {
    @Id
    @Column(name = "id_transaction_paiement" , length = 10)
    private String idTransactionPaiement;

    @Column(name = "id_transaction", unique = true, nullable = false)
    private String idTransaction;

    @Column(name = "pay_token")
    private String payToken;

    @Column(name = "operateur")
    private String operateur;

    @Enumerated(EnumType.STRING)
    private StatutPaiement statutAgregateur;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_miseAjour")
    private LocalDateTime dateMiseAJour;
}
