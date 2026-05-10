package com.ict300.P04.Service.paiement;

import com.ict300.P04.repository.interfaces.retraitCode.RetraitCodeInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class OtpCleanupScheduler {

    @Autowired
    private RetraitCodeInterface retraitCodeInterface;


    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onStartup() {
        log.info("--- CHECK-UP AU DÉMARRAGE : Vérification des anciens otp ---");
        executerNettoyage();
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    @Transactional
    public void scheduleDailyCleanup() {
        log.info("--- NETTOYAGE PLANIFIÉ CHAQUE SEMAINE DES ANCIENS OTP ---");
        executerNettoyage();
    }

    private void executerNettoyage() {
        log.info("Suppression des OTP expirés depuis plus de 7 jours...");

        Instant ilYaUneSemaine = Instant.now().plus(7, ChronoUnit.DAYS);
        retraitCodeInterface.deleteOldCodes(ilYaUneSemaine);

        log.info("🧹 [SCHEDULER] Nettoyage des OTP effectué avec succès.");
    }
}