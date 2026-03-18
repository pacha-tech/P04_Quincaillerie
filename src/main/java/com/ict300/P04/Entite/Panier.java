package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "\"Panier\"")
public class Panier {
    @Id
    @Column(name = "id_panier" , length = 10)
    private String idPanier;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_price")
    private Price price;

    @Column(name = "Quantity")
    private int quantity;

    @Column(name = "Date_added")
    private LocalDateTime dateAdded;
}
