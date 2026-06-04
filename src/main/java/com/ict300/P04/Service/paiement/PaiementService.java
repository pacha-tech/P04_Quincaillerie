package com.ict300.P04.Service.paiement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ict300.P04.DTO.paiement.aangaraPay.response.WebhookPayloadResponse;
import com.ict300.P04.DTO.paiement.response.PaiementResponseDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.CommandeAlreadyCancelledException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Service.commmande.CommandeService;
import com.ict300.P04.Service.paiement.aangaraPayService.AangaraPayService;
import com.ict300.P04.Service.paiement.sharePayService.SharePayService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.Utilitaires.StatutPaiement;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeInterface;
import com.ict300.P04.repository.interfaces.transaction.paiement.TransactionPaiementInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PaiementService {

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private CommandeInterface commandeInterface;

    @Autowired
    private DetailCommandeInterface detailCommandeInterface;

    @Autowired
    private LigneCommandeInterface ligneCommandeInterface;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private SharePayService sharePayService;

    @Autowired
    private AangaraPayService aangaraPayService;

    @Autowired
    private TransactionPaiementInterface transactionPaiementInterface;

    @Autowired
    private PaiementSseService paiementSseService;

    @Transactional
    public PaiementResponseDTO processPayment(String idCommande, String userId , String operateur, String phoneNumber) {

        User customer = customerInterface.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        Commande commande = commandeInterface.findById(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + idCommande));

        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException("Les détails de cette commande sont introuvables."));

        List<LigneCommande> ligneCommandes = ligneCommandeInterface.getDetailCommande(commande.getIdCommande());


        if (!commande.getUser().getIdUser().equals(userId)) {
            throw new UserNotFoundException("Cette commande ne vous appartient pas.");
        }
        if (commande.getStatut() == StatutCommande.PAYEE) {
            throw new IllegalStateException("La commande " + idCommande + " est déjà payée.");
        }
        if (StatutCommande.ANNULEE.equals(commande.getStatut())) {
            throw new CommandeAlreadyCancelledException("La commande est annulée.");
        }

        /*
        Map<String, String> infosPaiement = sharePayService.initierPaiementCheckout(
                commande.getIdCommande(),
                commande.getMontantTotal().doubleValue()
        );


        //AangaraPay pour le redirect
        Map<String, String> infosPaiement = aangraPayService.initierPaiementCheckout(
                commande.getIdCommande(),
                commande.getMontantTotal().doubleValue(),
                operateur
        );
         */

        Map<String, String> infosPaiement = aangaraPayService.initierPaiementDirect(
                commande.getIdCommande(),
                commande.getMontantTotal(),
                operateur,
                phoneNumber
        );


        //String paymentUrl = infosPaiement.get("paymentUrl");
        String sharepayReference = infosPaiement.get("transactionId");
        String apiMessage = infosPaiement.get("message");
        String payToken = infosPaiement.get("payToken");

        TransactionPaiement transactionPaiement = new TransactionPaiement();

        transactionPaiement.setIdTransactionPaiement(GenerateID.GenerateTransactionPaiementID());
        transactionPaiement.setIdTransaction(sharepayReference);
        transactionPaiement.setPayToken(payToken);
        transactionPaiement.setOperateur(operateur);
        transactionPaiement.setCommande(commande);
        transactionPaiement.setStatutAgregateur(StatutPaiement.PENDING);
        transactionPaiement.setDateCreation(LocalDateTime.now());

        transactionPaiementInterface.save(transactionPaiement);

        if(commande.getStatut() != StatutCommande.EN_ATTENTE_PAIEMENT) {
            commande.setStatut(StatutCommande.EN_ATTENTE_PAIEMENT);
            commandeInterface.save(commande);
        }

        PaiementResponseDTO response = new PaiementResponseDTO();
        response.setSuccess(true);
        response.setUrlTransaction(null);
        response.setTransactionId(sharepayReference);
        response.setMessage(apiMessage);

        return response;
    }

    /*
    //Gestion du webhook avec SharePay
    public void handleWebhook(String rawBody) throws JsonProcessingException, MessagingException {
        SharePayWebhookDTO payload = objectMapper.readValue(rawBody, SharePayWebhookDTO.class);
        String event = payload.getEvent();
        String idTransaction = payload.getData().getReference();
        String method = payload.getData().getPaymentMethod();

        switch (event) {
            case "payment.success":
                commandeService.confirmerPaiement(idTransaction , method);
                break;

            case "payment.failed":
                commandeService.echecPaiement(idTransaction);
                break;

            case "payment.cancelled":
                commandeService.annulerPaiement(idTransaction);
                break;

            case "payout.success":
                log.info("Virement sortant (Pay-Out) confirmé par l'agrégateur pour la référence : {}", idTransaction);
                // C'est ici que tu mettras à jour le statut final si tu suis l'état des virements quincailleries
                commandeService.confirmerTransfert(idTransaction);
                break;

            case "payout.failed":
                log.error("Échec critique du transfert des fonds chez l'agrégateur pour la référence : {}", idTransaction);
                // Logique spécifique d'alerte ou de marquage en BDD (ex: statut Virement_Echoué)
                break;

            default:
                log.info("Événement ignoré : {}", event);
        }
    }
     */

    public void handleWebhook(WebhookPayloadResponse payloadResponse) throws JsonProcessingException, MessagingException {

        boolean isDepot = payloadResponse.getPaytoken() != null && !payloadResponse.getPaytoken().isEmpty();

        if (isDepot) {
            traiterWebhookDepot(payloadResponse);
        } else {
            traiterWebhookRetrait(payloadResponse);
        }
    }

    private void traiterWebhookDepot(WebhookPayloadResponse payload) throws MessagingException {
        String idTransaction = payload.getTransaction_id();
        String method = payload.getOperator();

        StatutPaiement statutOfficiel = aangaraPayService.verifierStatutDepot(payload.getPaytoken());
        log.info("Webhook Dépôt - Statut officiel vérifié pour {} : {}", idTransaction, statutOfficiel);

        switch (statutOfficiel) {
            case SUCCESSFUL:
                commandeService.confirmerPaiement(idTransaction, method);
                paiementSseService.notifierChangementStatut(idTransaction , StatutPaiement.SUCCESSFUL);
                break;

            case FAILED:
                commandeService.echecPaiement(idTransaction);
                paiementSseService.notifierChangementStatut(idTransaction , StatutPaiement.FAILED);
                break;

            case PENDING:
                log.info("Paiement toujours en attente pour la transaction : {}", idTransaction);
                paiementSseService.notifierChangementStatut(idTransaction , StatutPaiement.PENDING);
                break;

            default:
                log.warn("Statut dépôt non géré : {}", statutOfficiel);
        }
    }


    private void traiterWebhookRetrait(WebhookPayloadResponse payload) {
        String idTransaction = payload.getTransaction_id();
        String method = payload.getOperator();

        StatutPaiement statutOfficiel = aangaraPayService.verifierStatutRetrait(idTransaction, method);
        log.info("Webhook Retrait - Statut officiel vérifié pour {} : {}", idTransaction, statutOfficiel);

        switch (statutOfficiel) {
            case SUCCESSFUL:
                log.info("✅ Virement sortant (Pay-Out) confirmé par l'agrégateur pour la référence : {}", idTransaction);
                commandeService.confirmerTransfert(idTransaction);
                break;

            case FAILED:
                log.error("❌ Échec critique du transfert des fonds chez l'agrégateur pour la référence : {}", idTransaction);
                // commandeService.marquerTransfertEchoue(idTransaction);
                break;

            case PENDING:
                log.info("⏳ Virement sortant toujours en attente chez l'opérateur pour : {}", idTransaction);
                break;

            default:
                log.warn("Statut retrait non géré : {}", statutOfficiel);
        }
    }
}