package com.ict300.P04.repository.interfacesImpl.campagnePromotion;

import com.google.api.client.util.DateTime;
import com.ict300.P04.Entite.CampagnePromotion;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.repository.interfaces.campagnePromotion.CampagnePromotionCustomInterface;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CampagnePromotionInterfaceImpl implements CampagnePromotionCustomInterface {

    @Autowired
    private EntityManager entityManager;

    @Override
    public CampagnePromotion getByIdCampagne(String idCampage) {
        String jpql = "SELECT c " +
                "FROM CampagnePromotion c " +
                "WHERE c.idCampagnePromotion = :id ";

        return entityManager.createQuery(jpql , CampagnePromotion.class)
                .setParameter("id" , idCampage)
                .getSingleResult();
    }

    @Override
    public List<Object[]> getByQuincaillerie(Quincaillerie quincaillerie) {
        String jpql = "SELECT c , COUNT(p)" +
                "FROM CampagnePromotion c " +
                "LEFT JOIN Promotion p ON p.campagnePromotion = c "+
                "WHERE c.quincaillerie = :id " +
                "GROUP BY c";

        return entityManager.createQuery(jpql , Object[].class)
                .setParameter("id" , quincaillerie)
                .getResultList();
    }

    /*
       @Override
public List<Object[]> getCampagnesAvecNombreProduits() {
    // Cette requête sélectionne l'objet Campagne et compte les occurrences de Price associés
    // On utilise un LEFT JOIN pour inclure les campagnes qui n'auraient pas encore de produits
    String jpql = "SELECT cp, COUNT(p.price.idPrice) " +
                  "FROM CampagnePromotion cp " +
                  "LEFT JOIN Promotion p ON p.campagnePromotion.idCampagne = cp.idCampagne " +
                  "GROUP BY cp " +
                  "ORDER BY cp.dateDebut DESC";

    return entityManager.createQuery(jpql, Object[].class)
            .getResultList();
}
    */
}
