package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "\"Historical_search\"")
public class HistoricalSearch {
    @Id
    @Column(name = "id_search")
    private String idHistoricalSearch;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "Search_date")
    private LocalDateTime searchDate;

    @Column(name = "Search")
    private String search;

    @Column(name = "City")
    private String city;
}
