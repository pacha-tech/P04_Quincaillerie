package com.ict300.P04.repository.interfaces.ligneCommande;

import com.ict300.P04.Entite.LigneCommande;

import java.util.List;

public interface LigneCommandeCustomInterface {
    List<LigneCommande> getDetailCommande(String idCommande);
}
