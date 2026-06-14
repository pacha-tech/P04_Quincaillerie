package com.ict300.P04.repository.interfacesImpl.product;

import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.repository.interfaces.product.ProductCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.engine.jdbc.env.internal.LobCreationLogging_$logger;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductInterfaceImpl implements ProductCustomInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findRecommendationByProductAndCategoryAndQuincaillerie(String categoryId, String productId, String quincaillerieId) {
        // Utilisation de pr.idPrice pour la jointure de promotion pour être plus précis
        String jpql = "SELECT p, pro.campagnePromotion.tauxRemise " +
                "FROM Product p " +
                "JOIN p.prices pr " +
                "LEFT JOIN Promotion pro ON pro.price.idPrice = pr.idPrice " +
                "AND pro.campagnePromotion.estActif = true " +
                "AND pro.campagnePromotion.dateDebut <= :now " +
                "AND pro.campagnePromotion.dateFin >= :now " +
                "WHERE p.category.idCategory = :catId " +
                "AND pr.quincaillerie.idQuincaillerie = :quinId " +
                "AND p.idProduct <> :prodId " +
                "ORDER BY FUNCTION('RAND')";

        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("catId", categoryId)
                .setParameter("quinId", quincaillerieId)
                .setParameter("prodId", productId)
                .setParameter("now", LocalDate.now())
                .setMaxResults(5)
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
    public Optional<Product> getProduct(String idProduct) {
        String jpql = "SELECT p " +
                "FROM Product p " +
                "WHERE p.idProduct = :id ";

        return entityManager.createQuery(jpql , Product.class)
                .setParameter("id",idProduct)
                .getResultList().stream().findFirst();
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

    @Override
    public List<Object[]> getProductByQuincailleries(Quincaillerie quincaillerie) {
        String jpql = "SELECT pr, p.campagnePromotion.tauxRemise " +
                "FROM Price pr " +
                "LEFT JOIN Promotion p ON p.price.idPrice = pr.idPrice " +
                "AND p.campagnePromotion.estActif = true " +
                "AND p.campagnePromotion.dateDebut <= :now " +
                "AND p.campagnePromotion.dateFin >= :now " +
                "WHERE pr.quincaillerie = :Quincaillerie";

        return entityManager.createQuery(jpql , Object[].class)
                .setParameter("Quincaillerie", quincaillerie)
                .setParameter("now", LocalDate.now())
                .getResultList();

    }

    @Override
    public List<Object[]> findByNameContainingIgnoreCase(String Name) {
        String jpql = "SELECT pr, p.campagnePromotion.tauxRemise " +
                "FROM Price pr " +
                "JOIN pr.product prod "+
                "LEFT JOIN Promotion p ON p.price.idPrice = pr.idPrice " +
                "AND p.campagnePromotion.estActif = true " +
                "AND p.campagnePromotion.dateDebut <= :now " +
                "AND p.campagnePromotion.dateFin >= :now " +
                "WHERE LOWER(prod.name) LIKE LOWER(CONCAT('%', :name, '%')) ";

        return entityManager.createQuery(jpql , Object[].class)
                .setParameter("name" , Name)
                .setParameter("now" , LocalDate.now())
                .getResultList();
    }

    @Override
    public Object[] findProductById(String idProduct) {
        String jpql = "SELECT pr , p.campagnePromotion.tauxRemise " +
                "FROM Price pr " +
                "LEFT JOIN Promotion p ON pr.product = p.price.product " +
                "AND p.campagnePromotion.estActif = true " +
                "AND p.campagnePromotion.dateDebut <= :now " +
                "AND p.campagnePromotion.dateFin >= :now " +
                "WHERE pr.idPrice = :id ";

        return entityManager.createQuery(jpql , Object[].class)
                .setParameter("id" , idProduct)
                .setParameter("now" , LocalDate.now())
                .getSingleResult();
    }

    @Override
    public List<Product> findOnlyName() {
        String jpql = "SELECT p " +
                "FROM Product p " +
                "WHERE p.idProduct IN (" +
                "SELECT MIN(p2.idProduct) " +
                "FROM Product p2 " +
                "GROUP BY p2.name)";

        return entityManager.createQuery(jpql, Product.class)
                .getResultList();
    }

    @Override
    public Integer countProductByQuincaillerie(String idQuincaillerie) {
        String jpql = " SELECT COUNT(p) " +
                "FROM Price p " +
                "WHERE p.quincaillerie.idQuincaillerie = :id ";

        Long result = entityManager.createQuery(jpql , Long.class)
                .setParameter("id" , idQuincaillerie)
                .getSingleResult();

        return result != null ? result.intValue() : 0;
    }
}
