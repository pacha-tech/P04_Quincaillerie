package com.ict300.P04.repository.interfaces.promotion;

import com.ict300.P04.Entite.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionInterface extends JpaRepository<Promotion , String> , PromotionCustomInterface {
}
