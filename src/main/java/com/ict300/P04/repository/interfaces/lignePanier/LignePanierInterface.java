package com.ict300.P04.repository.interfaces.lignePanier;

import com.ict300.P04.Entite.LignePanier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LignePanierInterface extends JpaRepository<LignePanier, String> , LignePanierCustomInterface {
}
