package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "HistoriqueNavigation")
public class HistoriqueNavigation {
    @Id
    @Column(name = "id_historique_navigation" , length = 10)
    private String idHistoriqueNavigation;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;

    @Column(name = "date_action")
    private LocalDateTime dateAction;

    @Column(name = "action_type")
    private String actionType;
}
