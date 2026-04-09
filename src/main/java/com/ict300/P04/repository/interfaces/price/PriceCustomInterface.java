package com.ict300.P04.repository.interfaces.price;

import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PriceCustomInterface {
    List<Price> searchAdvanced(String query, String city, String category, BigDecimal maxPrice);
    boolean ifAlreadyExistProductByQuincaillerie(String name , String quincaillerieId);
    Price getPriceByProductAndQuincaillerie(String produitId , String quincaillerieId);
    Price getByIdPrice(String idPrice);
    Price findByProductAndQuincaillerie(String idProduct , String idQuincaillerie);
    List<Price> findByQuincaillerie(String idQuincaillerie);
    List<Price> findPricesWithoutActivePromotion(String idQuincaillerie);
    List<Object[]> findPricesWithActivePromotion();
}
