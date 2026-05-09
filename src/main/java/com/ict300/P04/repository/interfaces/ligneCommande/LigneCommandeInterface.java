package com.ict300.P04.repository.interfaces.ligneCommande;

import com.ict300.P04.Entite.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneCommandeInterface extends JpaRepository<LigneCommande , String> , LigneCommandeCustomInterface {
}
