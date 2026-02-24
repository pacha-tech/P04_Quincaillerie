package com.ict300.P04.repository.interfacesImpl.category;

import com.ict300.P04.Entite.Category;
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
    public List<String> findNameOnly() {
        String jpql = "SELECT DISTINCT c.name " +
                "FROM Category c ";

        return entityManager.createQuery(jpql , String.class)
                .getResultList();
    }

    @Override
    public Category getCategoty(String idCategory) {
        String jpql = "SELECT c " +
                "FROM Category c " +
                "WHERE c.idCategory = :id ";

        return  entityManager.createQuery(jpql , Category.class)
                .setParameter("id",idCategory)
                .getSingleResult();
    }
}
