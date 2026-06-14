package com.ict300.P04.repository.interfaces.historiqueNavigation;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface HistoriqueNavigationCustomInterface {
    List<String> findTopCategoriesByUserId(String userId, Pageable pageable);
    List<String> findGlobalTopCategories(Pageable pageable);
}
