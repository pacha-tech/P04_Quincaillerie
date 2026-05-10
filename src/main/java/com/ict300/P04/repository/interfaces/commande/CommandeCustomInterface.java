package com.ict300.P04.repository.interfaces.commande;

import com.ict300.P04.Entite.Commande;

import java.time.LocalDateTime;
import java.util.List;

public interface CommandeCustomInterface {
    List<Commande> getAllCommandeByUser(String idUser);
    List<Commande> getAllCommandeByQuincaillerie(String idQuincaillerie);
    List<Commande> getCommandesByQuincaillerieAndDate(String idQuincaillerie , LocalDateTime startDate);
}
