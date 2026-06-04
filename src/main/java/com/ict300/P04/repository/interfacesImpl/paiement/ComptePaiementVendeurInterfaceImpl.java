package com.ict300.P04.repository.interfacesImpl.paiement;

import com.ict300.P04.Entite.ComptePaiementVendeur;
import com.ict300.P04.repository.interfaces.ComptePaiementVendeur.ComptePaiementVendeurCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ComptePaiementVendeurInterfaceImpl implements ComptePaiementVendeurCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ComptePaiementVendeur> getCompteByQuincaillerie(String idQuincaillerie) {
        String jpql = "SELECT c " +
                "FROM ComptePaiementVendeur c " +
                "WHERE c.quincaillerie.idQuincaillerie = :id ";

        return entityManager.createQuery(jpql , ComptePaiementVendeur.class)
                .setParameter("id" , idQuincaillerie)
                .getResultList().stream().findFirst();
    }
}
