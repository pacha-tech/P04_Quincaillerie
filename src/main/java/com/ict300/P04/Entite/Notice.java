package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "\"Notice\"")
public class Notice {

    @Id
    @Column(name = "id_notice")
    private String idNotice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_quincaillerie")
    private Quincaillerie quincaillerie;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "Comment")
    private String comment;

    @Column(name = "Response_quincaillerie")
    private String responseQuincaillerie;

    @Column(name = "Notice_date")
    private LocalDateTime noticeDate;

    @Column(name = "Rating")
    private int rating;
}
