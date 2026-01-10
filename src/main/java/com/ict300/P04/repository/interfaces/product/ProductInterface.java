package com.ict300.P04.repository.interfaces.product;

import com.ict300.P04.Entite.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInterface extends JpaRepository<Product , String> , ProductCustomInterface {
    List<Product> findByNameContainingIgnoreCase(String Name);

}
