package com.ict300.P04.Service.message;

import com.ict300.P04.DTO.message.response.MessageDTO;
import com.ict300.P04.DTO.message.request.IncomingMessageDTO;
import com.ict300.P04.Entite.Conversation;
import com.ict300.P04.Entite.Message;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.conversation.ConversationInterface;
import com.ict300.P04.repository.interfaces.message.MessageInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import com.ict300.P04.repository.interfaces.user.seller.SellerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private MessageInterface messageInterface;

    @Autowired
    private ConversationInterface conversationInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Transactional
    public MessageDTO enregistrerMessage(IncomingMessageDTO incomingMessageDTO , String idSender) {
        Conversation conversation;

        User sender = customerInterface.findById(idSender)
                .orElseThrow(() -> new ResourceNotFoundException("Expéditeur non trouvé"));

        if(incomingMessageDTO.getIdConversation() == null || incomingMessageDTO.getIdConversation().isEmpty()){
            Quincaillerie receiver = quincaillerieInterface.findById(incomingMessageDTO.getIdReceiver())
                    .orElseThrow(() -> new ResourceNotFoundException("Receveur non trouvé"));

            conversation = new Conversation();
            conversation.setIdConversation(GenerateID.GenerateConversationID());
            conversation.setSender(sender);
            conversation.setReceiver(receiver);

            conversation = conversationInterface.save(conversation);
        } else {
            conversation = conversationInterface.findById(incomingMessageDTO.getIdConversation())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation non trouvée"));
        }



        Message message = new Message();

        message.setIdMessage(GenerateID.GenerateMessageID());
        message.setContenu(incomingMessageDTO.getContenu());
        message.setConversation(conversation);
        message.setSender(sender);

        Message message1 = messageInterface.save(message);
        conversation.setLastMessage(message);

        MessageDTO messageDTO = new MessageDTO();

        messageDTO.setIdMessage(message1.getIdMessage());
        messageDTO.setIdSender(message1.getSender().getIdUser());
        messageDTO.setNomSender(message.getSender().getName());
        messageDTO.setContenu(message1.getContenu());
        messageDTO.setEstLu(message1.getEstLu());
        messageDTO.setIdConversation(conversation.getIdConversation());
        messageDTO.setLuAt(message1.getLuAt());
        messageDTO.setIdReceiver(incomingMessageDTO.getIdReceiver());

        return messageDTO;
    }

    @Transactional
    public List<MessageDTO> getAllMessageByConversation(String idConversation) {
        Conversation conversation = conversationInterface.findByIdConversation(idConversation);

        if(conversation == null ) {
            return null;
        }

        return conversation.getMessages().stream().map(message -> new MessageDTO(
                message.getIdMessage(),
                idConversation,
                conversation.getSender().getIdUser(),
                conversation.getReceiver().getIdQuincaillerie(),
                conversation.getSender().getName(),
                message.getContenu(),
                message.getEstLu(),
                message.getLuAt()
        )).toList();
    }
}
