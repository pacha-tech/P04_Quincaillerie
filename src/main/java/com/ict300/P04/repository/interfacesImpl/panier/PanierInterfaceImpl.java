package com.ict300.P04.repository.interfacesImpl.panier;

import com.ict300.P04.Entite.Panier;
import com.ict300.P04.Entite.User;
import com.ict300.P04.repository.interfaces.panier.PanierCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PanierInterfaceImpl implements PanierCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int ifAlreadyExistProdctInPanier(String idPrice, String idUser) {
        String jpql = "SELECT p.quantity " +
                "FROM Panier p " +
                "WHERE p.price.idPrice = :id1 " +
                "AND p.user.idUser = :id2";

        try {

            Integer quantity = entityManager.createQuery(jpql, Integer.class)
                    .setParameter("id1", idPrice)
                    .setParameter("id2", idUser)
                    .getSingleResult();

            return (quantity != null) ? quantity : 0;

        } catch (NoResultException e) {
            return 0;
        }
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

    @Override
    public List<Object[]> findPanierByUser(User user) {
        String jpql = "SELECT pa , p.campagnePromotion.tauxRemise " +
                "FROM Panier pa " +
                "LEFT JOIN Promotion p ON p.price.idPrice = pa.price.idPrice " +
                "AND p.campagnePromotion.estActif = true " +
                "AND p.campagnePromotion.dateDebut <= :now " +
                "AND p.campagnePromotion.dateFin >= :now " +
                "WHERE pa.user = :user";

        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("user", user)
                .setParameter("now", LocalDate.now())
                .getResultList();
    }

    @Override
    public Panier findProductInPanierByUser(String idUser, String idPrice) {
        String jpql = "SELECT p " +
                "FROM Panier p " +
                "WHERE p.user.idUser = :id1 " +
                "AND p.price.idPrice = :id2 ";

        return entityManager.createQuery(jpql , Panier.class)
                .setParameter("id1" , idUser)
                .setParameter("id2" , idPrice)
                .getSingleResult();
    }


}