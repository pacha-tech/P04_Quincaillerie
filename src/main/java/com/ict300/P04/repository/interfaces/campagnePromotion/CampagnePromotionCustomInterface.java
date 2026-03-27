package com.ict300.P04.repository.interfaces.campagnePromotion;

import com.google.api.client.util.DateTime;
import com.ict300.P04.Entite.CampagnePromotion;
import com.ict300.P04.Entite.Quincaillerie;

import java.util.List;

public interface CampagnePromotionCustomInterface {
    CampagnePromotion getByIdCampagne(String idCampage);
    List<Object[]> getByQuincaillerie(Quincaillerie quincaillerie);
}
