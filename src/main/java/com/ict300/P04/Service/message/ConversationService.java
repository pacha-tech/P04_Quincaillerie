package com.ict300.P04.Service.message;

import com.ict300.P04.DTO.message.response.ConversationDTO;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.repository.interfaces.conversation.ConversationInterface;
import com.ict300.P04.repository.interfaces.message.MessageInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationInterface conversationInterface;

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private MessageInterface messageInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    public List<ConversationDTO> getAllByQuincaillerie(String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie).orElse(null);
        if(quincaillerie == null ) throw new ResourceNotFoundException("Quincaillerie non existante");

        return conversationInterface.findByReceiverOrderByUpdatedAtDesc(quincaillerie).stream().map(conversation -> {

                    var lastMsg = conversation.getLastMessage();

                    return new ConversationDTO(
                            conversation.getIdConversation(),
                            conversation.getSender().getName(),
                            lastMsg != null ? lastMsg.getContenu() : "",
                            lastMsg != null ? lastMsg.getSender().getIdUser() : "",
                            lastMsg != null ? lastMsg.getEstLu() : false,
                            conversation.getUpdatedAt(),
                            messageInterface.countByConversationAndEstLuFalseAndSender_IdUserNot(conversation, quincaillerie.getIdQuincaillerie())
                    );
                }).toList();
    }

    public List<ConversationDTO> getAllByClient(String idUser) {
        User user = customerInterface.getByIdUser(idUser);
        if(user == null ) throw new UserNotFoundException("Utilisateur non existant");

        return conversationInterface.findBySenderOrderByUpdatedAtDesc(user).stream().map(conversation -> {

                    var lastMsg = conversation.getLastMessage();

                    return new ConversationDTO(
                            conversation.getIdConversation(),
                            conversation.getReceiver().getStoreName(),
                            lastMsg != null ? lastMsg.getContenu() : "",
                            lastMsg != null ? lastMsg.getSender().getIdUser() : "",
                            lastMsg != null ? lastMsg.getEstLu() : false,
                            conversation.getUpdatedAt(),
                            messageInterface.countByConversationAndEstLuFalseAndSender_IdUserNot(conversation, user.getIdUser())
                    );
                }).toList();
    }
}
