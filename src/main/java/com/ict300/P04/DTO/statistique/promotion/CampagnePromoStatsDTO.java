package com.ict300.P04.DTO.statistique.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data @AllArgsConstructor @NoArgsConstructor
public class CampagnePromoStatsDTO {
    private String idCampagne;
    private int nombreCommandes;
    private int totalArticlesVendus;
    private BigDecimal chiffreAffairesReel;
    private BigDecimal chiffreAffairesTheorique;
    private BigDecimal manqueAGagner;
    private int articlesVendusAvantPromo;
    private double pourcentageAugmentationVolume;
    private BigDecimal chiffreAffairesAvantPromo;
    private double pourcentageAugmentationRevenu;
}
