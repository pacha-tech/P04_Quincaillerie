package com.ict300.P04.repository.interfacesImpl.facture;

import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.Facture;
import com.ict300.P04.repository.interfaces.facture.FactureCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FactureInterfaceImpl implements FactureCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Facture> getFactureByCommande(String idCommande) {
        String jpql = "SELECT f " +
                "FROM Facture f " +
                "WHERE f.commande.idCommande = :id";

        return entityManager.createQuery(jpql , Facture.class)
                .setParameter("id" , idCommande)
                .getResultList().stream().findFirst();
    }
}
