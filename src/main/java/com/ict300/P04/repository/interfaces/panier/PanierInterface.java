package com.ict300.P04.repository.interfaces.panier;

import com.ict300.P04.Entite.Panier;
import com.ict300.P04.Entite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PanierInterface extends JpaRepository<Panier, String> , PanierCustomInterface {
    List<Panier> findPanierByUser(User user);
    void deletePanierByUser(User user);
}
