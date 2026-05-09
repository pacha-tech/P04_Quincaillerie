package com.ict300.P04.repository.interfacesImpl.OTP;

import com.ict300.P04.Entite.RetraitCode;
import com.ict300.P04.repository.interfaces.retraitCode.RetraitCodeCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class RetraitCodeInterfaceImpl implements RetraitCodeCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<RetraitCode> findByCommandeId(String idCommande) {
        String jpql = "SELECT rc " +
                "FROM RetraitCode rc " +
                " WHERE rc.commande.idCommande = :id AND rc.isValid = :id2 ";

        return entityManager.createQuery(jpql , RetraitCode.class)
                .setParameter("id" , idCommande)
                .setParameter("id2" , true)
                .getResultList().stream().findFirst();
    }

    @Override
    public void deleteOldCodes(LocalDateTime limit) {
        String jpql = "DELETE FROM RetraitCode rc " +
                "WHERE rc.expirationdate < :date ";

        entityManager.createQuery(jpql)
                .setParameter("date" , limit)
                .executeUpdate();
    }
}
