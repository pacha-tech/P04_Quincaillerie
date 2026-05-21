package com.ict300.P04.repository.interfacesImpl.stock;

import com.ict300.P04.Entite.Stock;
import com.ict300.P04.repository.interfaces.stock.StockCustomInterface;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StockInterfaceImpl implements StockCustomInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Stock> getStockProductByIdAndDate(String idPrice, LocalDateTime startDate) {
        String jpql = "SELECT s FROM Stock s " +
                "WHERE s.price.idPrice = :id " +
                "AND s.dateMouvement >= :startDate " +
                "ORDER BY s.dateMouvement ASC";

        return entityManager.createQuery(jpql, Stock.class)
                .setParameter("id", idPrice)
                .setParameter("startDate", startDate)
                .getResultList();
    }

    @Override
    public List<Stock> findPriceByIdOrderByDateMouvementDesc(String idPrice) {
        String jpql = "SELECT s FROM Stock s " +
                "WHERE s.price.idPrice = :id " +
                "ORDER BY s.dateMouvement DESC";

        return entityManager.createQuery(jpql, Stock.class)
                .setParameter("id", idPrice)
                .getResultList();
    }
}
