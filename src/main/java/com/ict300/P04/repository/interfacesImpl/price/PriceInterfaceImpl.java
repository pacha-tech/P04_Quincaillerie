package com.ict300.P04.repository.interfacesImpl.price;

import com.ict300.P04.Entite.Price;
import com.ict300.P04.repository.interfaces.price.PriceCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class PriceInterfaceImpl implements PriceCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Price> searchAdvanced(String query, String city, String category, BigDecimal maxPrice) {
        StringBuilder jpql = new StringBuilder("SELECT p FROM Price p");
        jpql.append(" JOIN p.quincaillerie q");
        jpql.append(" JOIN p.product pr");
        jpql.append(" WHERE 1=1");

        if(query != null && !query.isEmpty()){
            jpql.append(" AND LOWER(pr.name) LIKE LOWER(CONCAT('%' ,:query, '%'))");
        }
        if(city != null && !city.isEmpty()){
            jpql.append(" AND q.city = :city");
        }
        if (category != null && !category.isEmpty()) {
            jpql.append(" AND pr.category = :category");
        }
        if (maxPrice != null) {
            jpql.append(" AND p.price <= :maxPrice");
        }

        jpql.append(" ORDER BY p.price ASC");


        TypedQuery<Price> typedQuery = entityManager.createQuery(jpql.toString(), Price.class);

        if (query != null && !query.isEmpty()) {
            typedQuery.setParameter("query", query);
        }
        if (city != null && !city.isEmpty()) {
            typedQuery.setParameter("city", city);
        }
        if (category != null && !category.isEmpty()) {
            typedQuery.setParameter("category", category);
        }
        if (maxPrice != null) {
            typedQuery.setParameter("maxPrice", maxPrice);
        }

        return typedQuery.getResultList();
    }

    @Override
    public boolean ifAlreadyExistProductByQuincaillerie(String name, String quincaillerieId) {
        String jpql = " SELECT COUNT(pr) > 0 "+
        "FROM Price pr "+
        "JOIN pr.product p "+
        "JOIN pr.quincaillerie q "+
        "WHERE LOWER(p.name) = LOWER(:name) "+
         "AND q.idQuincaillerie = :id ";

        return entityManager.createQuery(jpql, Boolean.class)
                .setParameter("name", name)
                .setParameter("id", quincaillerieId)
                .getSingleResult();
    }

    @Override
    public Price getPriceByProductAndQuincaillerie(String produitId, String quincaillerieId) {
        String jpql = "SELECT p " +
                "FROM Price p " +
                "JOIN p.product pr " +
                "JOIN p.quincaillerie q " +
                "WHERE pr.idProduct = :product " +
                "AND q.idQuincaillerie = :quincaillerie ";

        return entityManager.createQuery(jpql , Price.class)
                .setParameter("product" , produitId)
                .setParameter("quincaillerie" , quincaillerieId)
                .getSingleResult();
    }
}
