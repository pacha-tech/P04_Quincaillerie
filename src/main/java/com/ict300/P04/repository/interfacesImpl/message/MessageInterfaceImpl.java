package com.ict300.P04.repository.interfacesImpl.message;

import com.ict300.P04.repository.interfaces.message.MessageCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class MessageInterfaceImpl implements MessageCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int markAsRead(List<String> idMessages) {
        String jpql = "UPDATE Message m " +
                "SET m.estLu = true " +
                "WHERE m.idMessage IN :ids";

        int count = entityManager.createQuery(jpql)
                .setParameter("ids" , idMessages)
                .executeUpdate();

        return count;
    }
}
