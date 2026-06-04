package com.ict300.P04.repository.interfacesImpl.paiement;

import com.ict300.P04.Entite.TransactionPaiement;
import com.ict300.P04.Utilitaires.StatutPaiement;
import com.ict300.P04.repository.interfaces.transaction.paiement.TransactionPaiementCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TransactionPaiementInterfaceImpl implements TransactionPaiementCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TransactionPaiement> getAllTransactionsWithStatusIsPending() {
        String jpql = "SELECT tp " +
                "FROM TransactionPaiement tp " +
                "WHERE tp.statutAgregateur = :status ";

        return entityManager.createQuery(jpql , TransactionPaiement.class)
                .setParameter("status" , StatutPaiement.PENDING)
                .getResultList();
    }

    @Override
    public Optional<TransactionPaiement> findByIdTransaction(String idTransaction) {
        String jpql = "SELECT tp " +
                "FROM TransactionPaiement tp " +
                "WHERE tp.idTransaction = :id ";

        return entityManager.createQuery(jpql , TransactionPaiement.class)
                .setParameter("id" , idTransaction)
                .getResultList().stream().findFirst();
    }
}
