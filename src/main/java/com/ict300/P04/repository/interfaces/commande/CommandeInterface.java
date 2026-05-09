package com.ict300.P04.repository.interfaces.commande;

import com.ict300.P04.Entite.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeInterface extends JpaRepository<Commande , String> , CommandeCustomInterface {
}