package com.ict300.P04.Service.statistiques;

import com.ict300.P04.DTO.statistique.promotion.CampagnePromoStatsDTO;
import com.ict300.P04.Entite.CampagnePromotion;
import com.ict300.P04.Entite.LigneCommande;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.campagnePromotion.CampagnePromotionInterface;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PromotionStatsService {

    @Autowired
    private LigneCommandeInterface ligneCommandeInterface;

    @Autowired
    private CampagnePromotionInterface campagnePromotionInterface;


    public CampagnePromoStatsDTO calculerStatsCampagne(String idCampagne) {

        List<LigneCommande> lignesPromo = ligneCommandeInterface.getLigneCommandeByCampagnePromotion(idCampagne);

        BigDecimal caReel = BigDecimal.ZERO;
        BigDecimal caTheorique = BigDecimal.ZERO;
        int totalArticlesVendus = 0;
        Set<String> commandesUniques = new HashSet<>();

        for (LigneCommande ligne : lignesPromo) {
            if (ligne.getCommande().getStatut() != StatutCommande.ANNULEE) {
                commandesUniques.add(ligne.getCommande().getIdCommande());
                totalArticlesVendus += ligne.getQuantity();

                BigDecimal quantite = BigDecimal.valueOf(ligne.getQuantity());

                if (ligne.getPricePaye() != null) {
                    caReel = caReel.add(ligne.getPricePaye().multiply(quantite));
                }
                if (ligne.getPriceDeBase() != null) {
                    caTheorique = caTheorique.add(ligne.getPriceDeBase().multiply(quantite));
                }
            }
        }

        BigDecimal manqueAGagner = caTheorique.subtract(caReel);


        // 1. Récupérer la campagne
        CampagnePromotion campagne = campagnePromotionInterface.findById(idCampagne)
                .orElseThrow(() -> new ResourceNotFoundException("Campagne introuvable "));

        // 2. Calculer la durée exacte à comparer
        LocalDateTime debutPromo = campagne.getDateDebut().atStartOfDay();
        LocalDateTime finPromo = (campagne.getDateFin().isBefore(LocalDate.now()))
                ? campagne.getDateFin().atTime(23, 59, 59)
                : LocalDateTime.now();

        long joursDePromo = ChronoUnit.DAYS.between(debutPromo, finPromo);
        if (joursDePromo <= 0) joursDePromo = 1; // Sécurité

        LocalDateTime finAvantPromo = debutPromo.minusSeconds(1);
        LocalDateTime debutAvantPromo = debutPromo.minusDays(joursDePromo);

        // 3. Récupérer les ID des produits concernés
        List<String> idsPrices = campagne.getPromotions().stream()
                .map(promo -> promo.getPrice().getIdPrice())
                .collect(Collectors.toList());

        // 4. Initialiser les variables d'avant promo
        int articlesVendusAvant = 0;
        BigDecimal caAvant = BigDecimal.ZERO;

        // 5. Faire la recherche dans la base de données SEULEMENT si on a des produits
        if (!idsPrices.isEmpty()) {
            List<LigneCommande> ventesAvant = ligneCommandeInterface.findVentesAvantPromo(idsPrices, debutAvantPromo, finAvantPromo);

            for (LigneCommande ligne : ventesAvant) {
                articlesVendusAvant += ligne.getQuantity();
                if (ligne.getPricePaye() != null) {
                    caAvant = caAvant.add(ligne.getPricePaye().multiply(BigDecimal.valueOf(ligne.getQuantity())));
                }
            }
        }

        double augmentationVolume = 0.0;
        if (articlesVendusAvant > 0) {
            augmentationVolume = ((double) (totalArticlesVendus - articlesVendusAvant) / articlesVendusAvant) * 100;
        } else if (totalArticlesVendus > 0) {
            augmentationVolume = 100.0;
        }

        double augmentationRevenu = 0.0;
        if (caAvant.compareTo(BigDecimal.ZERO) > 0) {
            augmentationRevenu = caReel.subtract(caAvant)
                    .divide(caAvant, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } else if (caReel.compareTo(BigDecimal.ZERO) > 0) {
            augmentationRevenu = 100.0;
        }

        CampagnePromoStatsDTO stats = new CampagnePromoStatsDTO();
        stats.setIdCampagne(idCampagne);
        stats.setNombreCommandes(commandesUniques.size());
        stats.setTotalArticlesVendus(totalArticlesVendus);
        stats.setChiffreAffairesReel(caReel);
        stats.setChiffreAffairesTheorique(caTheorique);
        stats.setManqueAGagner(manqueAGagner);

        // Les nouveaux champs de comparaison
        stats.setArticlesVendusAvantPromo(articlesVendusAvant);
        stats.setPourcentageAugmentationVolume(Math.round(augmentationVolume * 100.0) / 100.0);
        stats.setChiffreAffairesAvantPromo(caAvant);
        stats.setPourcentageAugmentationRevenu(Math.round(augmentationRevenu * 100.0) / 100.0);

        return stats;
    }
}
