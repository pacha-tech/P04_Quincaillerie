package com.ict300.P04.repository.interfaces.price;

import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PriceInterface extends JpaRepository<Price , String> , PriceCustomInterface {
}
