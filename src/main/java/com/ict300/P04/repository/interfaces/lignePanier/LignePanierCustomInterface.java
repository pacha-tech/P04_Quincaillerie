package com.ict300.P04.repository.interfaces.lignePanier;

import com.ict300.P04.Entite.LignePanier;
import com.ict300.P04.Entite.Panier;
import com.ict300.P04.Entite.User;

import java.util.List;

public interface LignePanierCustomInterface {
    void deleteProductInPanier(String idPrice , Panier panier);
    int findQuantityByProductInPanier(String idPrice , Panier panier);
    List<Object[]> findLignesPanierWithPromoByUser(User user);
    void deletePanierByQuincaillerie(String idQuincaillerie , User user);
    LignePanier getProductInPanier(String idprice , Panier panier);
    List<LignePanier> getAllProductInPanier(String idPanier);
    Object[] findIfproductIsInPromotion(String idPrice);
}
