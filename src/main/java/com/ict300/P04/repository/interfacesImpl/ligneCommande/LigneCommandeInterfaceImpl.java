package com.ict300.P04.repository.interfacesImpl.ligneCommande;

import com.ict300.P04.Entite.LigneCommande;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

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
}
