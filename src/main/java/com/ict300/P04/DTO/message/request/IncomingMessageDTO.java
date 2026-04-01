package com.ict300.P04.DTO.message.request;


import lombok.Data;

@Data
public class IncomingMessageDTO {
    private String idConversation;
    private String idReceiver;
    private String contenu;
}

