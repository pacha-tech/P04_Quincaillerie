package com.ict300.P04.repository.interfacesImpl.user;

import com.ict300.P04.Entite.User;
import com.ict300.P04.repository.interfaces.user.UserCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserInterfaceImpl implements UserCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User getUser(String idUser) {
        String jpql = "SELECT u " +
                "FROM User u " +
                "WHERE u.idUser = :id ";

        return entityManager.createQuery(jpql , User.class)
                .setParameter("id" , idUser)
                .getSingleResult();
    }
}
