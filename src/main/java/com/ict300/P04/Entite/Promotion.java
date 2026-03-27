package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;


@Table(name = "\"Promotion\"")
@Entity
@Data
public class Promotion {
    @Id
    @Column(name = "id_promotion" , length = 10)
    private String idPromotion;

    @ManyToOne
    @JoinColumn(name = "id_price")
    private Price price;

    @ManyToOne
    @JoinColumn(name = "id_campagnePromotion")
    private CampagnePromotion campagnePromotion;
}
