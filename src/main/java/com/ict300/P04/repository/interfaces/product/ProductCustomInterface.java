package com.ict300.P04.repository.interfaces.product;

import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductCustomInterface {
    List<Product> findRecommendationByProductAndCategoryAndQuincaillerie(String categoryId, String productId , String quincaillerieId);
    BigDecimal findPriceByQuincaillerie(String productId , String quincaillerieId);
    List<String> findNameOnly(String name);
    Product getProduct(String idProduct);
    List<Price> getProductByQuincaillerie(Quincaillerie quincaillerie);
    List<Object[]> getProductByQuincailleries(Quincaillerie quincaillerie);
    List<Object[]> findByNameContainingIgnoreCase(String Name);
}
