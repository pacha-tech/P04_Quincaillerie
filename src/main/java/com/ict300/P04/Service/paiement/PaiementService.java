package com.ict300.P04.Service.paiement;

import com.ict300.P04.DTO.paiement.request.PaiementRequestDTO;
import com.ict300.P04.DTO.paiement.response.PaiementResponseDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.CommandeAlreadyCancelledException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Service.facture.FactureService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.MouvementStock;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.stock.StockInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PaiementService {

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private CommandeInterface commandeInterface;

    @Autowired
    private FactureService factureService;

    @Autowired
    private PriceInterface priceInterface;

    @Autowired
    private DetailCommandeInterface detailCommandeInterface;

    @Autowired
    private LigneCommandeInterface ligneCommandeInterface;

    @Autowired
    private StockInterface stockInterface;

    @Transactional
    public PaiementResponseDTO processPayment(PaiementRequestDTO request, String userId) {

        User customer = customerInterface.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID : " + userId));

        Commande commande = commandeInterface.findById(request.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée avec l'ID : " + request.getCommandeId()));

        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(request.getCommandeId())
                .orElseThrow(() -> new ResourceNotFoundException("Les détails de cette commande sont introuvables."));

        List<LigneCommande> ligneCommandes = ligneCommandeInterface.getDetailCommande(commande.getIdCommande());


        if (!commande.getUser().getIdUser().equals(userId)) {
            throw new UserNotFoundException("Cette commande ne vous appartient pas.");
        }
        if (commande.getStatut() == StatutCommande.PAYEE) {
            throw new IllegalStateException("La commande " + request.getCommandeId() + " est déjà payée.");
        }
        if (StatutCommande.ANNULEE.equals(commande.getStatut())) {
            throw new CommandeAlreadyCancelledException("La commande est annulée.");
        }

        // Configuration de la transaction
        String transactionId = "TRX-" + System.currentTimeMillis();


        LocalDateTime datePaiement = LocalDateTime.now(ZoneId.of("Africa/Douala"));

        detailCommande.setDatePaiement(datePaiement);
        commande.setStatut(StatutCommande.PAYEE);

        detailCommandeInterface.save(detailCommande);
        commandeInterface.save(commande);

        List<Stock> stocks = new ArrayList<>();
        List<Price> prices = new ArrayList<>();


        for (LigneCommande lc : ligneCommandes) {
            Price price = priceInterface.findByIdPrice(lc.getPrice().getIdPrice())
                    .orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));


            if (price.getStock() < lc.getQuantity()) {
                throw new IllegalStateException("Stock insuffisant pour l'article ID : " + price.getIdPrice());
            }


            Stock stock = new Stock();
            stock.setIdStock(GenerateID.GenerateStockID());
            stock.setQuantity(lc.getQuantity());
            stock.setPrice(price);
            stock.setTypeMouvement(MouvementStock.SORTIE);
            stock.setComment("Paiement Commande #" + commande.getIdCommande());
            stock.setDateMouvement(datePaiement);
            stocks.add(stock);


            price.setStock(price.getStock() - lc.getQuantity());
            prices.add(price);
        }

        stockInterface.saveAll(stocks);
        priceInterface.saveAll(prices);


        PaiementResponseDTO response = new PaiementResponseDTO();
        response.setSuccess(true);
        response.setTransactionId(transactionId);
        response.setPaiementDate(datePaiement);

        try {
            String urlCloudinary = factureService.generateFacture(commande, customer, transactionId, request.getPaymentMethod());
            response.setFactureUrl(urlCloudinary);
            response.setMessage("Paiement réussi pour la commande " + commande.getIdCommande());
        } catch (Exception e) {
            response.setFactureUrl(null);
            response.setMessage("Paiement validé, mais erreur lors de la génération du reçu.");

            log.error("Erreur de génération de facture pour la commande [{}] : {}", commande.getIdCommande(), e.getMessage(), e);
        }

        return response;
    }
}