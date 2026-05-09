package com.ict300.P04.Service.paiement;

import com.ict300.P04.DTO.paiement.request.PaiementRequestDTO;
import com.ict300.P04.DTO.paiement.response.PaiementResponseDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.CommandeAlreadyCancelledException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Service.facture.FactureService; // NOUVEL IMPORT
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeInterface;
import com.ict300.P04.repository.interfaces.lignePanier.LignePanierInterface;
import com.ict300.P04.repository.interfaces.panier.PanierInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaiementService {

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private CommandeInterface commandeInterface;

    @Autowired
    private FactureService factureService;

    @Autowired
    private DetailCommandeInterface detailCommandeInterface;

    @Transactional
    public PaiementResponseDTO processPayment(PaiementRequestDTO request, String userId) {

        User customer = customerInterface.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));


        Commande commande = commandeInterface.findById(request.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + request.getCommandeId()));

        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(request.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Les détails de cette commande sont introuvables."));

        if (!commande.getUser().getIdUser().equals(userId)) {
            throw new UserNotFoundException("Cette commande ne vous appartient pas.");
        }

        if (commande.getStatut() == StatutCommande.PAYEE) {
            throw new IllegalStateException("La commande " + request.getCommandeId() + " est déjà payée.");
        }

        // 2. Simulation de la transaction (TRX unique pour le lot de paiement)
        String transactionId = "TRX-" + System.currentTimeMillis();
        LocalDateTime datePaiement = LocalDateTime.now();

        if(StatutCommande.ANNULEE.equals(commande.getStatut())){
            throw new CommandeAlreadyCancelledException("La commande est annulée");
        }

        detailCommande.setDatePaiement(datePaiement);
        commande.setStatut(StatutCommande.PAYEE);

        detailCommandeInterface.save(detailCommande);
        commandeInterface.save(commande);

        // 4. Génération des factures et construction de la liste de réponses
        PaiementResponseDTO response = new PaiementResponseDTO();

        response.setSuccess(true);
        response.setTransactionId(transactionId);
        response.setPaiementDate(datePaiement);
        response.setMessage("Paiement reussis pour la commande "+request.getCommandeId());

        try {

            String urlCloudinary = factureService.generateFacture(commande, customer, transactionId, request.getPaymentMethod());

            response.setSuccess(true);
            response.setFactureUrl(urlCloudinary);
            response.setMessage("Paiement réussi pour la commande " + commande.getIdCommande());
        } catch (Exception e) {

            response.setSuccess(true);
            response.setFactureUrl(null);
            response.setMessage("Paiement validé, mais erreur lors de la génération du reçu.");
            System.err.println("Erreur Facture [" + commande.getIdCommande() + "] : " + e.getMessage());
        }

        return response;
    }
}