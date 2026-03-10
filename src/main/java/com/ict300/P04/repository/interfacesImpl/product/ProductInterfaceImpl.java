package com.ict300.P04.repository.interfacesImpl.product;

import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.repository.interfaces.product.ProductCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class ProductInterfaceImpl implements ProductCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> findRecommendationByProductAndCategoryAndQuincaillerie(String categoryId, String productId, String quincaillerieId) {
        String jpql = "SELECT p FROM Product p " +
                "JOIN p.prices pr "+
                "WHERE p.category.idCategory = :catId " +
                "AND pr.quincaillerie.idQuincaillerie = :quinId " +
                "AND p.idProduct <> :prodId " +
                "ORDER BY FUNCTION('RAND')";

        return entityManager.createQuery(jpql, Product.class)
                .setParameter("catId", categoryId)
                .setParameter("quinId", quincaillerieId)
                .setParameter("prodId", productId)
                .setMaxResults(5) // Limite à 5 produits pour Flutter
                .getResultList();
    }

    @Override
    public BigDecimal findPriceByQuincaillerie(String productId, String quincaillerieId) {
        String jpql = "SELECT p.price FROM Price p WHERE p.product.idProduct = :prodId " +
                "AND p.idQuincaillerie = :quinId " ;

        return entityManager.createQuery(jpql, BigDecimal.class)
                .setParameter("prodId",productId)
                .setParameter("quinId",quincaillerieId)
                .getSingleResult();
    }

    @Override
    public List<String> findNameOnly(String name) {
        String jpql = "SELECT DISTINCT p.name " +
                "FROM Product p " ;

        return entityManager.createQuery(jpql , String.class)
                .getResultList();
    }

    @Override
    public Product getProduct(String idProduct) {
        String jpql = "SELECT p " +
                "FROM Product p " +
                "WHERE p.idProduct = :id ";

        return entityManager.createQuery(jpql , Product.class)
                .setParameter("id",idProduct)
                .getSingleResult();
    }

    @Override
    public List<Price> getProductByQuincaillerie(Quincaillerie quincaillerie) {
        String jpql = "SELECT p " +
                " FROM Price p " +
                "WHERE p.quincaillerie = :id ";

        return entityManager.createQuery(jpql , Price.class)
                .setParameter("id" , quincaillerie)
                .getResultList();
    }
}
