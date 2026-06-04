package com.ict300.P04.Service.sheduler;

import com.ict300.P04.Entite.TransactionPaiement;
import com.ict300.P04.Service.commmande.CommandeService;
import com.ict300.P04.Service.paiement.aangaraPayService.AangaraPayService;
import com.ict300.P04.Utilitaires.StatutPaiement;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.transaction.paiement.TransactionPaiementInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PaiementCheckupSheduler {

    @Autowired
    private TransactionPaiementInterface transactionPaiementInterface;

    @Autowired
    private CommandeInterface commandeInterface;

    @Autowired
    private AangaraPayService aangaraPayService;

    @Autowired
    private CommandeService commandeService;

    @Scheduled(fixedDelay = 300000)
    public void verifierPaiementsEnAttente() {
        log.info("⏳ [CRON] Lancement de la tâche : Vérification des transactions PENDING...");

        List<TransactionPaiement> transactionsPending = transactionPaiementInterface.getAllTransactionsWithStatusIsPending();

        if (transactionsPending.isEmpty()) {
            log.info("✅ [CRON] Aucune transaction en attente. Fin de la tâche.");
            return;
        }

        log.info("🔍 [CRON] {} transaction(s) en attente trouvée(s). Début de la vérification avec AangaraaPay...", transactionsPending.size());

        for (TransactionPaiement tx : transactionsPending) {
            try {
                if (tx.getPayToken() == null) continue;

                StatutPaiement statutReel = aangaraPayService.verifierStatutDepot(tx.getPayToken());

                if (statutReel != StatutPaiement.PENDING) {
                    log.info("🔄 [CRON] Mise à jour transaction {} : PENDING -> {}", tx.getIdTransaction(), statutReel);

                    if (statutReel == StatutPaiement.SUCCESSFUL) {
                        log.info("🛍️ [CRON] Commande #{} validée (PAYEE) grâce au rattrapage automatique.", tx.getIdTransaction());

                        commandeService.confirmerPaiement(tx.getIdTransaction() , tx.getOperateur());
                    }
                    else if (statutReel == StatutPaiement.FAILED) {
                        // Optionnel : Tu peux passer la commande en ECHOUEE ou la laisser en ATTENTE_DE_PAIEMENT
                        // pour que le client retente avec une nouvelle transaction.
                        log.info("❌ [CRON] Commande #{} : Le paiement a finalement échoué chez l'agrégateur.", tx.getIdTransaction());
                    }
                }
            } catch (Exception e) {
                // Un try/catch à l'intérieur de la boucle pour qu'une erreur sur une transaction ne bloque pas les autres !
                log.error("⚠️ [CRON] Erreur lors de la vérification de la transaction {} : {}", tx.getIdTransaction(), e.getMessage());
            }
        }

        log.info("🏁 [CRON] Fin de la tâche de vérification.");
    }
}
