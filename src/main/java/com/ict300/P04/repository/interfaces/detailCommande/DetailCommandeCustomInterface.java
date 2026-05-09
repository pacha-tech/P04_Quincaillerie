package com.ict300.P04.repository.interfaces.detailCommande;

import com.ict300.P04.Entite.DetailCommande;

import java.util.Optional;

public interface DetailCommandeCustomInterface {
    Optional<DetailCommande> getDetailCommandeByCommande(String idCommande);
}
