package com.ict300.P04.repository.interfacesImpl.systemNotification;

import com.ict300.P04.repository.interfaces.systemNotification.SystemNotificationCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class SystemNotificationInterfaceImpl implements SystemNotificationCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void markAllAsRead(String idQuincaillerie) {
        String jpql = "UPDATE SystemNotification s " +
                "SET s.isRead = true " +
                "WHERE s.quincaillerie.idQuincaillerie = :id AND s.isRead = false ";

        entityManager.createQuery(jpql)
                .setParameter("id" , idQuincaillerie)
                .executeUpdate();
    }
}
