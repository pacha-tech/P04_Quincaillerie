package com.ict300.P04.Service.commmande;

import com.ict300.P04.DTO.commande.response.CommandeDetailDTO;
import com.ict300.P04.DTO.commande.response.CommandeResponseDTO;
import com.ict300.P04.DTO.commande.response.getCommandeDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.CommandeAlreadyCancelledException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.StoreMismatchException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.facture.FactureInterface;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeInterface;
import com.ict300.P04.repository.interfaces.lignePanier.LignePanierInterface;
import com.ict300.P04.repository.interfaces.panier.PanierInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommandeService {

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private PanierInterface panierInterface;

    @Autowired
    private LignePanierInterface lignePanierInterface;

    @Autowired
    private CommandeInterface commandeInterface;

    @Autowired
    private DetailCommandeInterface detailCommandeInterface;

    @Autowired
    private FactureInterface factureInterface;

    @Autowired
    private LigneCommandeInterface ligneCommandeInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Transactional
    public List<CommandeResponseDTO> processCommande(String idQuincaillerie, String userId) {

        User user = customerInterface.getByIdUser(userId)
                .orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(user.getIdUser())
                .orElseThrow(() -> new ResourceNotFoundException("Cet utilisateur n'a pas de panier valide"));

        List<LignePanier> allProductInPanier = lignePanierInterface.getAllProductInPanier(panier.getIdPanier());

        List<LignePanier> lignesAPayer = allProductInPanier;
        if (idQuincaillerie != null && !idQuincaillerie.isEmpty()) {
            lignesAPayer = allProductInPanier.stream()
                    .filter(ligne -> ligne.getPrice().getQuincaillerie().getIdQuincaillerie().equals(idQuincaillerie))
                    .toList();
        }

        if (lignesAPayer.isEmpty()) {
            throw new ResourceNotFoundException("Aucun produit à payer.");
        }

        Map<Quincaillerie, List<LignePanier>> lignesParQuincaillerie = lignesAPayer.stream()
                .collect(Collectors.groupingBy(ligne -> ligne.getPrice().getQuincaillerie()));


        List<CommandeResponseDTO> responses = new ArrayList<>();

        for (Map.Entry<Quincaillerie, List<LignePanier>> entry : lignesParQuincaillerie.entrySet()) {
            Quincaillerie quincaillerie = entry.getKey();
            List<LignePanier> articlesDeCetteQuincaillerie = entry.getValue();

            double totalDeCetteCommande = 0.0;
            List<LigneCommande> lignesPreparation = new ArrayList<>();

            for (LignePanier lignePanier : articlesDeCetteQuincaillerie) {
                Object[] promoData = lignePanierInterface.findIfproductIsInPromotion(lignePanier.getPrice().getIdPrice());
                double tauxRemise = ((Number) promoData[1]).doubleValue();

                double prixUnitaireBase = lignePanier.getPrice().getPrice().doubleValue();
                double prixFinalUnitaire = prixUnitaireBase * (1 - (tauxRemise / 100.0));

                totalDeCetteCommande += (prixFinalUnitaire * lignePanier.getQuantity());

                LigneCommande lc = new LigneCommande();

                lc.setIdLigneCommande(GenerateID.GenerateLigneCommandeID());
                lc.setPrice(lignePanier.getPrice());
                lc.setQuantity(lignePanier.getQuantity());
                lc.setHistoricalPrice(BigDecimal.valueOf(prixFinalUnitaire));

                lignesPreparation.add(lc);
            }

            Commande commande = new Commande();
            String nouvelIdCommande = GenerateID.GenerateCommandeID();

            commande.setIdCommande(nouvelIdCommande);
            commande.setUser(user);
            commande.setQuincaillerie(quincaillerie);
            commande.setMontantTotal(BigDecimal.valueOf(totalDeCetteCommande));
            commande.setStatut(StatutCommande.EN_ATTENTE_VALIDATION);

            Commande commandeSauvegardee = commandeInterface.save(commande);

            for (LigneCommande lc : lignesPreparation) {
                lc.setCommande(commandeSauvegardee);
                ligneCommandeInterface.save(lc);
            }

            DetailCommande dc = new DetailCommande();

            dc.setIdDetailCommande(GenerateID.GenerateDetailCommandeID());
            dc.setDateCommande(LocalDateTime.now());
            dc.setCommande(commandeSauvegardee);
            detailCommandeInterface.save(dc);



            responses.add(new CommandeResponseDTO(
                    nouvelIdCommande,
                    "Commande enregistrée pour " + quincaillerie.getStoreName()
            ));
        }


        lignePanierInterface.deleteAllInBatch(lignesAPayer);

        return responses;
    }

    public List<getCommandeDTO> getAllCommandeByUser(String userId) {
        User user = customerInterface.getByIdUser(userId)
                .orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        List<Commande> commandes = commandeInterface.getAllCommandeByUser(userId);

        List<getCommandeDTO> response = new ArrayList<>();

        for( Commande commande : commandes) {

            String url = factureInterface.getFactureByCommande(commande.getIdCommande())
                    .map(Facture::getUrlFacture)
                    .orElse(null);

            LocalDateTime dateCommande = detailCommandeInterface.getDetailCommandeByCommande(commande.getIdCommande())
                    .map(DetailCommande::getDateCommande)
                    .orElse(null);

            getCommandeDTO getCommande = new getCommandeDTO(
                    commande.getIdCommande(),
                    dateCommande,
                    commande.getStatut(),
                    commande.getMontantTotal().doubleValue(),
                    commande.getLigneCommandes().size(),
                    commande.getQuincaillerie().getStoreName(),
                    url,
                    null
            );

            response.add(getCommande);
        }
        return response;
    }

    public List<CommandeDetailDTO> getDetailCommande(String idCommande) {
        Commande commande = commandeInterface.findById(idCommande).orElseThrow(() -> new ResourceNotFoundException("Cette commande n'existe pas"));

         return ligneCommandeInterface.getDetailCommande(idCommande).stream().map(ligneCommande -> new CommandeDetailDTO(
                 ligneCommande.getPrice().getProduct().getName(),
                 ligneCommande.getHistoricalPrice().doubleValue(),
                 ligneCommande.getQuantity()
         )).toList();
    }

    public List<getCommandeDTO> getCommandeByQuincaillerie(String quincaillerieId) {
        Quincaillerie quincaillerie = quincaillerieInterface.findById(quincaillerieId)
                .orElseThrow(() -> new ResourceNotFoundException("La quincaillerie n'existe pas"));

        List<Commande> commandes = commandeInterface.getAllCommandeByQuincaillerie(quincaillerieId);

        List<getCommandeDTO> response = new ArrayList<>();

        for( Commande commande : commandes) {

            String url = factureInterface.getFactureByCommande(commande.getIdCommande())
                    .map(Facture::getUrlFacture)
                    .orElse(null);

            LocalDateTime dateCommande = detailCommandeInterface.getDetailCommandeByCommande(commande.getIdCommande())
                    .map(DetailCommande::getDateCommande)
                    .orElse(null);

            getCommandeDTO getCommande = new getCommandeDTO(
                    commande.getIdCommande(),
                    dateCommande,
                    commande.getStatut(),
                    commande.getMontantTotal().doubleValue(),
                    commande.getLigneCommandes().size(),
                    commande.getQuincaillerie().getStoreName(),
                    url,
                    commande.getUser().getName()
            );

            response.add(getCommande);
        }
        return response;
    }

    public void validateCommandeByQuincaillerie(String idCommande, String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.findById(idQuincaillerie).orElseThrow(() -> new ResourceNotFoundException("La quincaillerie n'existe pas"));

        Commande commande = commandeInterface.findById(idCommande).orElseThrow(() -> new ResourceNotFoundException("La commande n'existe pas"));

        if (!commande.getQuincaillerie().getIdQuincaillerie().equals(idQuincaillerie)) {
            throw new StoreMismatchException("Accès refusé : Cette commande n'appartient pas à votre quincaillerie.");
        }

        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException("Les détails de cette commande sont introuvables."));

        if(StatutCommande.ANNULEE.equals(commande.getStatut())){
            throw new CommandeAlreadyCancelledException("La commande est deja annulée");
        }

        detailCommande.setDateValidation(LocalDateTime.now());

        commande.setStatut(StatutCommande.EN_ATTENTE_PAIEMENT);

        detailCommandeInterface.save(detailCommande);
        commandeInterface.save(commande);
    }

    public void cancelCommandeByQuincaillerie(String idCommande, String quincaillerieId) {
        Quincaillerie quincaillerie = quincaillerieInterface.findById(quincaillerieId).orElseThrow(() -> new ResourceNotFoundException("La quincaillerie n'existe pas"));

        Commande commande = commandeInterface.findById(idCommande).orElseThrow(() -> new ResourceNotFoundException("La commande n'existe pas"));

        if (!commande.getQuincaillerie().getIdQuincaillerie().equals(quincaillerieId)) {
            throw new StoreMismatchException("Accès refusé : Cette commande n'appartient pas à votre quincaillerie.");
        }

        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(idCommande)
                .orElseThrow(() -> new ResourceNotFoundException("Les détails de cette commande sont introuvables."));

        if(StatutCommande.ANNULEE.equals(commande.getStatut())){
            throw new CommandeAlreadyCancelledException("La commande est deja annulée");
        }

        detailCommande.setDateAnnulation(LocalDateTime.now());

        commande.setStatut(StatutCommande.ANNULEE);

        detailCommandeInterface.save(detailCommande);
        commandeInterface.save(commande);
    }
}
