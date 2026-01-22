package com.ict300.P04.repository.interfacesImpl.category;

import com.ict300.P04.repository.interfaces.category.CategoryCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryInterfaceImpl implements CategoryCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> findNameOnly(String name) {
        String jpql = "SELECT DISTINCT c.name " +
                "FROM Category c " +
                "WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))";

        return entityManager.createQuery(jpql , String.class)
                .setParameter("query",name)
                .setMaxResults(3)
                .getResultList();
    }
}
