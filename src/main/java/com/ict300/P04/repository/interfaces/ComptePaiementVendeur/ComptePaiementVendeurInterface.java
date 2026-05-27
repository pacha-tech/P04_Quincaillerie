package com.ict300.P04.repository.interfaces.ComptePaiementVendeur;

import com.ict300.P04.Entite.ComptePaiementVendeur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComptePaiementVendeurInterface extends JpaRepository<ComptePaiementVendeur , String> , ComptePaiementVendeurCustomInterface {
}
