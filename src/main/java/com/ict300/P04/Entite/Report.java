package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Report {
    @Id
    @Column(name = "id_report")
    private String idReport;

    @ManyToOne
    @JoinColumn(name = "id_price")
    private Price price;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "Report_date")
    private LocalDateTime reportDate;
}
