package com.ict300.P04.DTO.message.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class IncomingMessageDTO {
    private String idConversation;
    private String idReceiver;
    private String contenu;
}

