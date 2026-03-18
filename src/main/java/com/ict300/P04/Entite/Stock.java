package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Table(name = "\"Stock\"")
@Entity
@Data
public class Stock {
    @Id
    @Column(name = "id_stock" , length = 10)
    private String idStock;

    @ManyToOne
    @JoinColumn(name = "id_price")
    private Price price;

    @Column(name = "Type_mouvement")
    private String typeMouvement;

    @Column(name = "Quantity")
    private int quantity;

    @Column(name = "Date_mouvement")
    private LocalDateTime dateMouvement;

    @Column(name = "Comment")
    private String comment;
}
