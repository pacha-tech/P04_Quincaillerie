package com.ict300.P04.repository.interfaces.panier;


import com.ict300.P04.Entite.Panier;
import com.ict300.P04.Entite.User;

import java.util.List;

public interface PanierCustomInterface {
    int ifAlreadyExistProdctInPanier(String idPrice , String idUser);
    void deleteProductInPanier(String idPrice , String idUser);
    void deletePanierByQuincaillerie(String idQuincaillerie , String idUser);
    List<Object[]> findPanierByUser(User user);
    Panier findProductInPanierByUser(String idUser , String idPrice);
}
