package com.ict300.P04.Entite;

import com.ict300.P04.Utilitaires.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "SystemNotification")
@Data
public class SystemNotification {
    @Id
    @Column(name = "id_notification", length = 10)
    private String idSystemNotification;

    @Column(name = "Message", nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false, length = 30)
    private NotificationType type;

    @Column(name = "Is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "Created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quincaillerie", nullable = true)
    private Quincaillerie quincaillerie;


    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
