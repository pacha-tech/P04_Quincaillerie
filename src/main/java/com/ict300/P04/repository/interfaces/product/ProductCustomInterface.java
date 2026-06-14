package com.ict300.P04.repository.interfaces.product;

import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.Stock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductCustomInterface {
    List<Object[]> findRecommendationByProductAndCategoryAndQuincaillerie(String categoryId, String productId , String quincaillerieId);
    BigDecimal findPriceByQuincaillerie(String productId , String quincaillerieId);
    List<String> findNameOnly(String name);
    Optional<Product> getProduct(String idProduct);
    List<Price> getProductByQuincaillerie(Quincaillerie quincaillerie);
    List<Object[]> getProductByQuincailleries(Quincaillerie quincaillerie);
    List<Object[]> findByNameContainingIgnoreCase(String Name);
    Object[] findProductById(String idProduct);
    List<Product> findOnlyName();
    Integer countProductByQuincaillerie(String idQuincaillerie);
}
