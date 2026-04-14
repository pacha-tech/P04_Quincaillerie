package com.ict300.P04.Entite;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "Message")
@Data
public class Message {

    @Id
    @Column(name = "id_message", length = 10)
    private String idMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conversation", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sender", nullable = false)
    private User sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "est_lu")
    private Boolean estLu = false;

    @Column(name = "lu_at")
    private LocalDateTime luAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
