package com.ict300.P04.repository.interfaces.detailCommande;

import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.DetailCommande;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailCommandeInterface extends JpaRepository<DetailCommande , String> , DetailCommandeCustomInterface {
    List<DetailCommande> findByCommandeIn(List<Commande> commandes);
}
