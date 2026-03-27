package com.ict300.P04.repository.interfaces.campagnePromotion;

import com.ict300.P04.Entite.CampagnePromotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampagnePromotionInterface extends JpaRepository<CampagnePromotion , String> , CampagnePromotionCustomInterface {
}
