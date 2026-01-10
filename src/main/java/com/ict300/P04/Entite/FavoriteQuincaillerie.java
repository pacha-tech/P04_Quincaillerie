package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "\"Favorite_quincaillerie\"")
public class FavoriteQuincaillerie {
    @Id
    @Column(name = "id_favorite_quincaillerie" , length = 10)
    private String idFavoriteQuincaillerie;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_quincaillerie")
    private Quincaillerie quincaillerie;

    @Column(name = "Date_added")
    private LocalDateTime dateAdded;
}
