package com.ict300.P04.repository.interfaces.facture;

import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactureInterface extends JpaRepository<Facture , String> , FactureCustomInterface {
    List<Facture> findByCommandeIn(List<Commande> commandes);
}
