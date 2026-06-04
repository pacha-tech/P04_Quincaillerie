package com.ict300.P04.repository.interfaces.transaction.paiement;

import com.ict300.P04.Entite.TransactionPaiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionPaiementInterface extends JpaRepository<TransactionPaiement , String> , TransactionPaiementCustomInterface {
}
