package com.ict300.P04.repository.interfacesImpl.user;

import com.ict300.P04.Entite.User;
import com.ict300.P04.repository.interfaces.user.customer.CustomerCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CustomerInterfaceImpl implements CustomerCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> getByIdUser(String idUser) {
        String jpql = "SELECT u " +
                "FROM User u " +
                "WHERE u.idUser = :id ";

        return entityManager.createQuery(jpql , User.class)
                .setParameter("id" , idUser)
                .getResultList().stream().findFirst();
    }
}
