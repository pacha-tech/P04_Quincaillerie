package com.ict300.P04.repository.interfacesImpl.lignePanier;

import com.ict300.P04.Entite.LignePanier;
import com.ict300.P04.Entite.Panier;
import com.ict300.P04.Entite.User;
import com.ict300.P04.repository.interfaces.lignePanier.LignePanierCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class LignePanierInterfaceImpl implements LignePanierCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void deleteProductInPanier(String idPrice , Panier panier) {
        String jpql = "DELETE " +
                "FROM LignePanier lp " +
                "WHERE lp.price.idPrice = :id1 AND lp.panier = :id2 ";

        entityManager.createQuery(jpql)
                .setParameter("id1" , idPrice)
                .setParameter("id2" , panier)
                .executeUpdate();
    }

    @Override
    public int findQuantityByProductInPanier(String idPrice, Panier panier) {
        String jpql = "SELECT lp.quantity " +
                "FROM LignePanier lp " +
                "WHERE lp.price.idPrice = :id1 " +
                "AND lp.panier = :id2";

        Integer quantity = entityManager.createQuery(jpql, Integer.class)
                .setParameter("id1", idPrice)
                .setParameter("id2", panier)
                .getSingleResult();

        return (quantity != null) ? quantity : 0;
    }

    @Override
    public List<Object[]> findLignesPanierWithPromoByUser(User user) {
        String jpql = "SELECT lp, COALESCE(p.campagnePromotion.tauxRemise, 0.0) " +
                "FROM LignePanier lp " +
                "JOIN lp.panier pa " +
                "LEFT JOIN Promotion p ON p.price.idPrice = lp.price.idPrice " +
                "AND p.campagnePromotion.estActif = true " +
                "AND p.campagnePromotion.dateDebut <= :now " +
                "AND p.campagnePromotion.dateFin >= :now " +
                "WHERE pa.user = :user ";
        return entityManager.createQuery(jpql , Object[].class)
                .setParameter("user" , user)
                .setParameter("now" , LocalDate.now())
                .getResultList();
    }

    @Override
    public void deletePanierByQuincaillerie(String idQuincaillerie, User user) {
        String jpql = "DELETE " +
                "FROM LignePanier lp " +
                "WHERE lp.price.quincaillerie.idQuincaillerie = :id1 AND lp.panier.user = :id2 ";

        entityManager.createQuery(jpql)
                .setParameter("id1" , idQuincaillerie)
                .setParameter("id2" , user)
                .executeUpdate();
    }

    @Override
    public LignePanier getProductInPanier(String idPrice, Panier panier) {
        String jpql = "SELECT lp " +
                "FROM LignePanier lp " +
                "WHERE lp.price.idPrice = :id1 " +
                "AND lp.panier = :id2";

        return entityManager.createQuery(jpql, LignePanier.class)
                .setParameter("id1", idPrice)
                .setParameter("id2", panier)
                .getSingleResult();
    }

    @Override
    public List<LignePanier> getAllProductInPanier(String idPanier) {
        String jpql = "SELECT lp " +
                "FROM LignePanier lp " +
                "WHERE lp.panier.idPanier = :id ";

        return entityManager.createQuery(jpql , LignePanier.class)
                .setParameter("id" , idPanier)
                .getResultList();
    }

    @Override
    public Object[] findIfproductIsInPromotion(String idPrice) {
        String jpql = "SELECT pr , COALESCE(p.campagnePromotion.tauxRemise, 0.0) " +
                "FROM Price pr " +
                "LEFT JOIN Promotion p ON p.price.idPrice = pr.idPrice " +
                "AND p.campagnePromotion.estActif = true " +
                "AND p.campagnePromotion.dateDebut <= :now " +
                "AND p.campagnePromotion.dateFin >= :now " +
                "WHERE pr.idPrice = :idPrice";

        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("idPrice", idPrice)
                .setParameter("now", LocalDate.now())
                .getSingleResult();
    }
}
