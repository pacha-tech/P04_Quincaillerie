package com.ict300.P04.repository.interfaces.transaction.versement;

import com.ict300.P04.Entite.TransactionVersement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionVersementInterface extends JpaRepository<TransactionVersement , String> , TransactionVersementCustomInterface {
}
