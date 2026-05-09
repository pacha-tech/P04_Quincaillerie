package com.ict300.P04.repository.interfaces.detailCommande;

import com.ict300.P04.Entite.DetailCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailCommandeInterface extends JpaRepository<DetailCommande , String> , DetailCommandeCustomInterface {
}
