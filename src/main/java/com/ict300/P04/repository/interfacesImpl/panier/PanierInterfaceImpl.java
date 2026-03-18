package com.ict300.P04.repository.interfacesImpl.panier;

import com.ict300.P04.repository.interfaces.panier.PanierCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class PanierInterfaceImpl implements PanierCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean ifAlreadyExistProdctInPanier(String idPrice, String idUser) {
        String jpql = "SELECT COUNT(p) " +
                "FROM Panier p " +
                "WHERE p.price.idPrice = :id1 " +
                "AND p.user.idUser = :id2";

        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("id1", idPrice)
                .setParameter("id2", idUser)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public void deleteProductInPanier(String idPrice , String idUser) {
        String jpql = "DELETE " +
                "FROM Panier p " +
                "WHERE p.price.idPrice = :id1 AND p.user.idUser = :id2 ";

        entityManager.createQuery(jpql)
                .setParameter("id1" , idPrice)
                .setParameter("id2" , idUser)
                .executeUpdate();
    }

    @Override
    public void deletePanierByQuincaillerie(String idQuincaillerie, String idUser) {
        String jpql = "DELETE " +
                "FROM Panier p " +
                "WHERE p.price.quincaillerie.idQuincaillerie = :id1 AND p.user.idUser = :id2 ";

        entityManager.createQuery(jpql)
                .setParameter("id1" , idQuincaillerie)
                .setParameter("id2" , idUser)
                .executeUpdate();
    }
}
