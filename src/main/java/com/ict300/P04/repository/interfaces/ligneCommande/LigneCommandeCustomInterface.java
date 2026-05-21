package com.ict300.P04.repository.interfaces.ligneCommande;

import com.ict300.P04.Entite.LigneCommande;

import java.time.LocalDateTime;
import java.util.List;

public interface LigneCommandeCustomInterface {
    List<LigneCommande> getDetailCommande(String idCommande);
    List<LigneCommande> getLigneCommandeByCampagnePromotion(String idCampagnePromotion);
    List<LigneCommande> findVentesAvantPromo(List<String> idsPrices, LocalDateTime debutAvantPromo, LocalDateTime finAvantPromo);
}
