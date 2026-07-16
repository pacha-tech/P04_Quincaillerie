package com.ict300.P04.repository.interfacesImpl.paiement;

import com.ict300.P04.Entite.TransactionVersement;
import com.ict300.P04.repository.interfaces.transaction.versement.TransactionVersementCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

public class TransactionVersementinterfaceImpl implements TransactionVersementCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    /*
    @Override
    public Optional<TransactionVersement> findByIdTransaction(String idTransaction) {
        String jpql = "SELECT tp " +
                "FROM TransactionPaiement tp " +
                "WHERE tp.idTransaction = :id ";

        return entityManager.createQuery(jpql , TransactionVersement.class)
                .setParameter("id" , idTransaction)
                .getResultList().stream().findFirst();
    }
     */
}
