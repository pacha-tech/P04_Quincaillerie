package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "RetraitCode")
public class RetraitCode {
    @Id
    @Column(name = "id_retrait_code", length = 10)
    private String idRetraitCode;

    @Column(name = "code_hash")
    private String codeHash;

    @Column(name = "expiration_date")
    private Instant expirationdate;

    @Column(name = "tentatives_echouees")
    private int tentativesEchouees;

    @Column(name = "is_valid")
    private boolean isValid;

    @OneToOne
    @JoinColumn(name = "id_commande")
    private Commande commande;
}
