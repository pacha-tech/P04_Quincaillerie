package com.ict300.P04.DTO.message.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data  @AllArgsConstructor @NoArgsConstructor
public class ConversationDTO {
    private String idConversation;
    private String nameReceiver;
    private String lastMessage;
    private LocalDateTime updateAt;
}
