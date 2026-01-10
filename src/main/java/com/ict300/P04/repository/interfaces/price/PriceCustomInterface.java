package com.ict300.P04.repository.interfaces.price;

import com.ict300.P04.Entite.Price;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface PriceCustomInterface {
    List<Price> searchAdvanced(String query, String city, String category, BigDecimal maxPrice);
}
