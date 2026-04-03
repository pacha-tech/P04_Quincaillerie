package com.ict300.P04.repository.interfaces.campagnePromotion;

import com.ict300.P04.Entite.CampagnePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampagnePromotionInterface extends JpaRepository<CampagnePromotion , String> , CampagnePromotionCustomInterface {
    List<CampagnePromotion> findCampagnePromotionByEstActifTrueAndDateFinBefore(LocalDate now);
}
