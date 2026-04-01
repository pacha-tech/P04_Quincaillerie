package com.ict300.P04.DTO.message.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor
public class MessageDTO {
    private String idMessage;
    private String idConversation;
    private String idSender;
    private String idReceiver;
    private String nomSender;
    private String contenu;
    private Boolean estLu;
    private LocalDateTime luAt;
}
