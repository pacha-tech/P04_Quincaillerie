package com.ict300.P04.repository.interfaces.transaction.paiement;

import com.ict300.P04.Entite.TransactionPaiement;

import java.util.List;
import java.util.Optional;

public interface TransactionPaiementCustomInterface {
    List<TransactionPaiement> getAllTransactionsWithStatusIsPending();
    Optional<TransactionPaiement> findByIdTransaction(String idTransaction);
}
