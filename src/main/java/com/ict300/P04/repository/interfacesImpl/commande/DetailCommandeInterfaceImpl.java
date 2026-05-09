package com.ict300.P04.repository.interfacesImpl.commande;

import com.ict300.P04.Entite.DetailCommande;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DetailCommandeInterfaceImpl implements DetailCommandeCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<DetailCommande> getDetailCommandeByCommande(String idCommande) {
        String jpql = "SELECT dc " +
                "FROM DetailCommande dc " +
                "WHERE dc.commande.idCommande = :id ";

        return entityManager.createQuery(jpql , DetailCommande.class)
                .setParameter("id" , idCommande)
                .getResultList().stream().findFirst();
    }
}
