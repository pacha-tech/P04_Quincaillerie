package com.ict300.P04.repository.interfaces.ComptePaiementVendeur;

import com.ict300.P04.Entite.ComptePaiementVendeur;

import java.util.Optional;

public interface ComptePaiementVendeurCustomInterface {
    Optional<ComptePaiementVendeur> getCompteByQuincaillerie(String idQuincaillerie);
}
