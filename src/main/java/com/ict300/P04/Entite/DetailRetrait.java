package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "DetailRetrait")
public class DetailRetrait {
    @Id
    @Column(name = "id_detail_retrait", length = 10)
    private String idDetailRetrait;

    @Column(name = "ip_vendeur", length = 45)
    private String ipVendeur;

    @Column(name = "user_agent_vendeur", length = 512)
    private String userAgentVendeur;

    @OneToOne
    @JoinColumn(name = "id_commande")
    private Commande commande;
}
