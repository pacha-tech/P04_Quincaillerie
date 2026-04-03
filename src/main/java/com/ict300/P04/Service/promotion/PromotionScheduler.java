package com.ict300.P04.Service.promotion;

import com.ict300.P04.Entite.CampagnePromotion;
import com.ict300.P04.repository.interfaces.campagnePromotion.CampagnePromotionInterface;
import jakarta.annotation.PostConstruct; // Import pour le démarrage
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PromotionScheduler {

    @Autowired
    private CampagnePromotionInterface campagneInterface;


    @PostConstruct
    public void onStartup() {
        log.info("--- CHECK-UP AU DÉMARRAGE : Vérification des promotions ---");
        performCleanup();
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyCleanup() {
        log.info("--- NETTOYAGE PLANIFIÉ (MINUIT) ---");
        performCleanup();
    }


    @Transactional
    public void performCleanup() {
        LocalDate now = LocalDate.now();

        List<CampagnePromotion> expired = campagneInterface.findCampagnePromotionByEstActifTrueAndDateFinBefore(now);

        if (!expired.isEmpty()) {
            expired.forEach(cp -> {
                cp.setEstActif(false);
                log.info("Désactivation de la promotion : {}", cp.getNom());
            });
            campagneInterface.saveAll(expired);
            log.info("Total : {} promotions désactivées avec succès.", expired.size());
        } else {
            log.info("Aucune promotion expirée détectée.");
        }
    }
}