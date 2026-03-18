package com.ict300.P04.repository.interfaces.panier;


import com.ict300.P04.Entite.Panier;

import java.util.List;

public interface PanierCustomInterface {
    boolean ifAlreadyExistProdctInPanier(String idPrice , String idUser);
    void deleteProductInPanier(String idPrice , String idUser);
    void deletePanierByQuincaillerie(String idQuincaillerie , String idUser);
}
