package com.ict300.P04.repository.interfaces.conversation;

import com.ict300.P04.Entite.Conversation;
import com.ict300.P04.Entite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationInterface  extends JpaRepository<Conversation , String > , ConversationCustomInterface {
    List<Conversation> findConversationBySender(User sender);
    Conversation findByIdConversation(String idConversation);
}
