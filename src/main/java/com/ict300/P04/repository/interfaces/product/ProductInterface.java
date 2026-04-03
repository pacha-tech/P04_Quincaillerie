package com.ict300.P04.repository.interfaces.product;

import com.ict300.P04.Entite.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductInterface extends JpaRepository<Product , String> , ProductCustomInterface {
}
