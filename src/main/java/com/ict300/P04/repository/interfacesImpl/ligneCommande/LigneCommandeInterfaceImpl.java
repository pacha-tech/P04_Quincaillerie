package com.ict300.P04.repository.interfacesImpl.ligneCommande;

import com.ict300.P04.Entite.LigneCommande;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LigneCommandeInterfaceImpl implements LigneCommandeCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<LigneCommande> getDetailCommande(String idCommande) {
        String jpql = "SELECT lc " +
                "FROM LigneCommande lc " +
                "WHERE lc.commande.idCommande = :id ";

        return entityManager.createQuery(jpql , LigneCommande.class)
                .setParameter("id" , idCommande)
                .getResultList();
    }

    @Override
    public List<LigneCommande> getLigneCommandeByCampagnePromotion(String idCampagnePromotion) {
        String jpql = "SELECT lc FROM LigneCommande lc " +
                "WHERE lc.campagnePromotion.idCampagnePromotion = :id ";

        return entityManager.createQuery(jpql , LigneCommande.class)
                .setParameter("id" , idCampagnePromotion)
                .getResultList();
    }


    @Override
    public List<LigneCommande> findVentesAvantPromo(List<String> idsPrices, LocalDateTime debutAvantPromo, LocalDateTime finAvantPromo) {

        if (idsPrices == null || idsPrices.isEmpty()) {
            return new ArrayList<>();
        }

        String jpql = "SELECT l FROM LigneCommande l " +
                "WHERE l.price.idPrice IN :idsPrices " +
                "AND l.commande.detailCommande.dateCommande BETWEEN :debut AND :fin " +
                "AND l.commande.statut IN (:payee , :livree)";

        return entityManager.createQuery(jpql, LigneCommande.class)
                .setParameter("idsPrices", idsPrices)
                .setParameter("debut", debutAvantPromo)
                .setParameter("fin", finAvantPromo)
                .setParameter("payee", StatutCommande.PAYEE)
                .setParameter("livree", StatutCommande.LIVREE)
                .getResultList();
    }
}
