package com.ict300.P04.repository.interfaces.product;

import com.ict300.P04.Entite.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductCustomInterface {
    List<Product> findRecommendationByProductAndCategoryAndQuincaillerie(String categoryId, String productId , String quincaillerieId);
    BigDecimal findPriceByQuincaillerie(String productId , String quincaillerieId);
}
