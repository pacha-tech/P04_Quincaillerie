package com.ict300.P04.repository.interfacesImpl.commande;

import com.ict300.P04.Entite.Commande;
import com.ict300.P04.repository.interfaces.commande.CommandeCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommandeInterfaceImpl implements CommandeCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Commande> getAllCommandeByUser(String idUser) {
        String jpql = "SELECT c " +
                "FROM Commande c " +
                "WHERE c.user.idUser = :id ";

        return entityManager.createQuery(jpql , Commande.class)
                .setParameter("id" , idUser)
                .getResultList();
    }

    @Override
    public List<Commande> getAllCommandeByQuincaillerie(String idQuincaillerie) {
        String jpql = "SELECT c " +
                "FROM Commande c " +
                "WHERE c.quincaillerie.idQuincaillerie = :id ";

        return entityManager.createQuery(jpql , Commande.class)
                .setParameter("id" , idQuincaillerie)
                .getResultList();
    }
}
