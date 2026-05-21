package com.ict300.P04.repository.interfaces.stock;

import com.ict300.P04.Entite.Stock;

import java.time.LocalDateTime;
import java.util.List;

public interface StockCustomInterface {
    List<Stock> getStockProductByIdAndDate(String idPrice , LocalDateTime startDate);

    List<Stock> findPriceByIdOrderByDateMouvementDesc(String idPrice);
}
