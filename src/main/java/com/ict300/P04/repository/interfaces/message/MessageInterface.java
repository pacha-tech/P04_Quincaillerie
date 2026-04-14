package com.ict300.P04.repository.interfaces.message;

import com.ict300.P04.Entite.Conversation;
import com.ict300.P04.Entite.Message;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageInterface extends JpaRepository<Message , String> , MessageCustomInterface {
    long countByConversationAndEstLuFalseAndSender_IdUserNot(Conversation conversation, String idCurrentUser);
    Message findByIdMessage(String idMessage);
}
