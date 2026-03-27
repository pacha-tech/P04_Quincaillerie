package com.ict300.P04.repository.interfaces.promotion;

import com.ict300.P04.Entite.Promotion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface PromotionCustomInterface {
    Boolean existsActivePromoForPeriod(String idPrice, LocalDate DateDebut , LocalDate DateFin);
}
