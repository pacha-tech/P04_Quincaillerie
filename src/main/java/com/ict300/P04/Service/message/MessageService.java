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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
            conversation.setCreatedAt(LocalDateTime.now());

            conversation = conversationInterface.saveAndFlush(conversation);
        } else {
            conversation = conversationInterface.findById(incomingMessageDTO.getIdConversation())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation non trouvée"));
        }


        Message message = new Message();

        message.setIdMessage(GenerateID.GenerateMessageID());
        message.setContenu(incomingMessageDTO.getContenu());
        message.setConversation(conversation);
        message.setSender(sender);
        message.setCreatedAt(LocalDateTime.now());

        Message message1 = messageInterface.saveAndFlush(message);
        conversation.setLastMessage(message);

        conversationInterface.save(conversation);

        MessageDTO messageDTO = new MessageDTO();

        messageDTO.setIdMessage(message1.getIdMessage());
        messageDTO.setIdSender(message1.getSender().getIdUser());
        messageDTO.setNomSender(message.getSender().getName());
        messageDTO.setContenu(message1.getContenu());
        messageDTO.setEstLu(message1.getEstLu());
        messageDTO.setIdConversation(conversation.getIdConversation());
        messageDTO.setLuAt(message1.getLuAt());
        messageDTO.setCreatedAt(message1.getCreatedAt());

        String realReceiverId = conversation.getSender().getIdUser().equals(idSender)
                ? conversation.getReceiver().getIdQuincaillerie()
                : conversation.getSender().getIdUser();

        messageDTO.setIdReceiver(realReceiverId);

        return messageDTO;
    }

    @Transactional
    public List<MessageDTO> getAllMessageByConversation(String idConversation) {
        Conversation conversation = conversationInterface.findByIdConversation(idConversation);

        if(conversation == null ) {
            return null;
        }

        return conversation.getMessages().stream().sorted(Comparator.comparing(Message::getCreatedAt))
                .map(message -> {

                    // On vérifie si l'auteur du message est le client qui a créé la conversation
                    boolean isClientTheAuthor = message.getSender().getIdUser().equals(conversation.getSender().getIdUser());

                    // Si le client parle, le destinataire est la boutique. Sinon, le destinataire est le client.
                    String realReceiverId = isClientTheAuthor
                            ? conversation.getReceiver().getIdQuincaillerie()
                            : conversation.getSender().getIdUser();

                    return new MessageDTO(
                            message.getIdMessage(),
                            idConversation,
                            message.getSender().getIdUser(),
                            realReceiverId,
                            message.getSender().getName(),
                            message.getContenu(),
                            message.getEstLu(),
                            message.getLuAt(),
                            message.getCreatedAt()
                    );
                }).toList();
    }

    @Transactional
    public void readConfirmation(List<String> idMessages , String idReceiver) {
        if (idMessages == null || idMessages.isEmpty()) {
            return;
        }

        String idExpediteur = "";

        try {
            messageInterface.markAsRead(idMessages);

            Message message = messageInterface.findByIdMessage(idMessages.get(0));

            if(message != null ){
                Conversation conversation = message.getConversation();
                if(Objects.equals(conversation.getSender().getIdUser(), idReceiver)){
                    idExpediteur = conversation.getReceiver().getIdQuincaillerie();
                }else {
                    idExpediteur = conversation.getSender().getIdUser();
                }
                messagingTemplate.convertAndSend("/canal/readReceipts/" + idExpediteur, message.getConversation().getIdConversation());
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur SQL lors de la confirmation de lecture : " + e.getMessage());

            throw new RuntimeException("Échec de la mise à jour des messages", e);
        }
    }
}
