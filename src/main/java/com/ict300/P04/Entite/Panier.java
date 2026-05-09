package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "Panier")
public class Panier {
    @Id
    @Column(name = "id_panier" , length = 10)
    private String idPanier;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "Creation_date")
    private  LocalDateTime creationDate;

    @Column(name = "Update_date")
    private  LocalDateTime updateDate;

    @OneToMany(mappedBy = "panier")
    List<LignePanier> lignePaniers = new ArrayList<>();

    public void removeLignePanier(LignePanier ligne) {
        this.lignePaniers.remove(ligne);
        ligne.setPanier(null);
    }
}
