package com.ict300.P04.repository.interfaces.facture;

import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.Facture;

import java.util.Optional;

public interface FactureCustomInterface {
    Optional<Facture> getFactureByCommande(String idCommande);
}
