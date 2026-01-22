package com.ict300.P04.repository.interfaces.price;

import com.ict300.P04.Entite.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceInterface extends JpaRepository<Price , String> , PriceCustomInterface {

}
