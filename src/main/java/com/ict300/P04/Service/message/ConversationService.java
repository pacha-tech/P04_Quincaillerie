package com.ict300.P04.Service.message;

import com.ict300.P04.DTO.message.response.ConversationDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.repository.interfaces.conversation.ConversationInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationInterface conversationInterface;

    @Autowired
    private CustomerInterface customerInterface;

    public List<ConversationDTO> getAllConversationByUser(String idUser) {

        User user = customerInterface.getByIdUser(idUser);

        if(user == null ) {
            throw new UserNotFoundException("Utilisateur non existant");
        }

        return conversationInterface.findConversationBySender(user).stream().map(conversation -> new ConversationDTO(
                conversation.getIdConversation(),
                conversation.getReceiver().getStoreName(),
                conversation.getLastMessage().getContenu(),
                conversation.getUpdatedAt()
        )).toList();
    }
}
