package com.ict300.P04.repository.interfacesImpl.historiqueNavigation;

import com.ict300.P04.repository.interfaces.historiqueNavigation.HistoriqueNavigationCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HistoriqueNavigationInterfaceImpl implements HistoriqueNavigationCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> findTopCategoriesByUserId(String userId, Pageable pageable) {
        String jpql = "SELECT h.category.idCategory " +
                "FROM HistoriqueNavigation h " +
                "WHERE h.user.idUser = :id AND h.category IS NOT NULL " +
                "GROUP BY h.category.idCategory " +
                "ORDER BY COUNT(h.category.idCategory) DESC ";

        return entityManager.createQuery(jpql , String.class)
                .setParameter("id" , userId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    @Override
    public List<String> findGlobalTopCategories(Pageable pageable) {
        String jpql = "SELECT h.category.idCategory " +
                "FROM HistoriqueNavigation h " +
                "WHERE h.category.idCategory IS NOT NULL " +
                "GROUP BY h.category.idCategory " +
                "ORDER BY COUNT(h.category.idCategory) DESC ";

        return entityManager.createQuery(jpql , String.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}
