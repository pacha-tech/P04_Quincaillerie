package com.ict300.P04.DTO.notification;

import com.ict300.P04.Utilitaires.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class SystemNotificationDTO {
    private String idNotification;
    private String message;
    private NotificationType type;
    private String targetId;
    private boolean isRead;
    private LocalDateTime createdAt;
}

