package com.ict300.P04.repository.interfaces.transaction.versement;

import com.ict300.P04.Entite.TransactionVersement;

import java.util.Optional;

public interface TransactionVersementCustomInterface {
    Optional<TransactionVersement> findByIdTransaction(String idTransaction);
}
