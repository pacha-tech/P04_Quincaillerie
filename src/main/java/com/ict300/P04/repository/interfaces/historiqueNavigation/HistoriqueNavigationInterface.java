package com.ict300.P04.repository.interfaces.historiqueNavigation;

import com.ict300.P04.Entite.HistoriqueNavigation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoriqueNavigationInterface extends JpaRepository<HistoriqueNavigation , String> , HistoriqueNavigationCustomInterface {
}
