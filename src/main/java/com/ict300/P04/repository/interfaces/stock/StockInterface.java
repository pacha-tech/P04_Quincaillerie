package com.ict300.P04.repository.interfaces.stock;

import com.ict300.P04.Entite.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockInterface extends JpaRepository<Stock , Stock> , StockCustomInterface {
}
