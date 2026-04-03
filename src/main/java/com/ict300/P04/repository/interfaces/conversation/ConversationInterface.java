package com.ict300.P04.repository.interfaces.conversation;

import com.ict300.P04.Entite.Conversation;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationInterface  extends JpaRepository<Conversation , String > , ConversationCustomInterface {
    Conversation findByIdConversation(String idConversation);
    //List<Conversation> findBySenderOrReceiverOrderByUpdatedAtDesc(User user);
    List<Conversation> findBySenderOrderByUpdatedAtDesc(User user);
    List<Conversation> findByReceiverOrderByUpdatedAtDesc(Quincaillerie quincaillerie);

}
