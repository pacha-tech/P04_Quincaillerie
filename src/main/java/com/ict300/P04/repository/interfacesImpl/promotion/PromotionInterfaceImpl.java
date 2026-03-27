package com.ict300.P04.repository.interfacesImpl.promotion;

import com.ict300.P04.Entite.Promotion;
import com.ict300.P04.repository.interfaces.promotion.PromotionCustomInterface;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PromotionInterfaceImpl implements PromotionCustomInterface {

    @Autowired
    EntityManager entityManager;

    @Override
    public Boolean existsActivePromoForPeriod(String idPrice, LocalDate dateDebut, LocalDate dateFin) {
        String jpql = "SELECT COUNT(p) FROM Promotion p " +
                "WHERE p.price.idPrice = :id1 " +
                "AND p.campagnePromotion.estActif = true " +
                "AND p.campagnePromotion.dateDebut <= :id2 " +
                "AND p.campagnePromotion.dateFin >= :id3 ";

        long count = entityManager.createQuery(jpql , Long.class)
                .setParameter("id1" , idPrice)
                .setParameter("id2" , dateDebut)
                .setParameter("id3" , dateFin)
                .getSingleResult();

        return count > 0;
    }

}
