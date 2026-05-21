package com.ict300.P04.Service.paiement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ict300.P04.DTO.paiement.response.PaiementResponseDTO;
import com.ict300.P04.DTO.paiement.sharePay.webHook.SharePayWebhookDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.CommandeAlreadyCancelledException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Service.commmande.CommandeService;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ObjectMapper objectMapper;

    @Transactional
    public PaiementResponseDTO processPayment(String idCommande, String userId) {

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


        Map<String, String> infosPaiement = sharePayService.initierPaiementCheckout(
                commande.getIdCommande(),
                commande.getMontantTotal().doubleValue()
        );

        String paymentUrl = infosPaiement.get("paymentUrl");
        String sharepayReference = infosPaiement.get("reference");
        String apiMessage = infosPaiement.get("message");


        commande.setIdTransaction(sharepayReference);
        commandeInterface.save(commande);

        PaiementResponseDTO response = new PaiementResponseDTO();
        response.setSuccess(true);
        response.setUrlTransaction(paymentUrl);
        response.setTransactionId(sharepayReference);
        response.setMessage(apiMessage);

        return response;
    }

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
}