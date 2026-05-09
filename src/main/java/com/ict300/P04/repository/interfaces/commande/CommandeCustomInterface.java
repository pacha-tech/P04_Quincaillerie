package com.ict300.P04.repository.interfaces.commande;

import com.ict300.P04.Entite.Commande;

import java.util.List;

public interface CommandeCustomInterface {
    List<Commande> getAllCommandeByUser(String idUser);
    List<Commande> getAllCommandeByQuincaillerie(String idQuincaillerie);
}
