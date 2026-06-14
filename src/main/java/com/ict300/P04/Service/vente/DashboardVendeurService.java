package com.ict300.P04.Service.vente;

import com.ict300.P04.DTO.quincaillerie.dashboard.response.*;
import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.LigneCommande;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Exception.QuincaillerieNotFoundException;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardVendeurService {

    @Autowired
    private CommandeInterface commandeInterface;

    @Autowired
    private ProductInterface productInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    public Dashboard getDashboardVendeur(String idQuincaillerie , int period) {
        Quincaillerie quincaillerie = quincaillerieInterface.findById(idQuincaillerie)
                .orElseThrow(() -> new QuincaillerieNotFoundException("Cette Quincaillerie n'existe pas"));
        List<Commande> commandes = commandeInterface.getAllCommandeByQuincaillerie(idQuincaillerie);

        ChiffreAffaires chiffreAffaires = getChiffreAffaire(commandes , period);
        Reputation reputation = getReputation(quincaillerie);
        Integer stock = productInterface.countProductByQuincaillerie(idQuincaillerie);

        StatistiquesCles statistiquesCles = new StatistiquesCles(chiffreAffaires , reputation , stock);
        BigDecimal fondEnAttente = getFondEnAttente(commandes);
        List<TopProduit> topProduits = getTopProduits(commandes , period);

        return new Dashboard(statistiquesCles , fondEnAttente , topProduits);
    }

    public ChiffreAffaires getChiffreAffaire(List<Commande> commandeList , int period) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(period);
        LocalDateTime previousStartDate = LocalDateTime.now().minusDays(period * 2);
        BigDecimal caActuel = BigDecimal.ZERO;
        BigDecimal caPrecedent = BigDecimal.ZERO;

        if (commandeList != null) {
            for (Commande c : commandeList) {

                if (c.getDetailCommande() != null && c.getDetailCommande().getDatePaiement() != null && c.getMontantTotal() != null) {

                    LocalDateTime datePaiement = c.getDetailCommande().getDatePaiement();

                    boolean estValidee = (c.getStatut() == StatutCommande.PAYEE || c.getStatut() == StatutCommande.LIVREE);

                    if (estValidee) {
                        if(!datePaiement.isBefore(startDate)){
                            caActuel = caActuel.add(c.getMontantTotal());
                        } else if (!datePaiement.isBefore(previousStartDate) && datePaiement.isBefore(startDate)) {
                            caPrecedent = caPrecedent.add(c.getMontantTotal());
                        }
                    }
                }
            }
        }

        double evolutionPourcentage = 0.0;
        String tendance = "up";

        if (caPrecedent.compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal difference = caActuel.subtract(caPrecedent);

            BigDecimal division = difference.divide(caPrecedent, 4, RoundingMode.HALF_UP);

            evolutionPourcentage = division.multiply(new BigDecimal("100")).doubleValue();

        } else if (caActuel.compareTo(BigDecimal.ZERO) > 0) {
            evolutionPourcentage = 100.0;
        }

        if (evolutionPourcentage < 0) {
            tendance = "down";
        }

        return new ChiffreAffaires(
                caActuel,
                Math.round(evolutionPourcentage * 10.0) / 10.0,
                tendance
        );
    }


    private List<TopProduit> getTopProduits(List<Commande> commandeList, int period) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(period);

        Map<String, Integer> ventesParProduit = new HashMap<>();

        Map<String, LigneCommande> detailsProduit = new HashMap<>();

        if (commandeList != null) {
            for (Commande c : commandeList) {
                if (c.getDetailCommande() != null && c.getDetailCommande().getDatePaiement() != null) {
                    LocalDateTime datePaiement = c.getDetailCommande().getDatePaiement();
                    boolean estValidee = (c.getStatut() == StatutCommande.PAYEE || c.getStatut() == StatutCommande.LIVREE);

                    if (estValidee && !datePaiement.isBefore(startDate)) {

                        if (c.getLigneCommandes() != null) {
                            for (LigneCommande ligne : c.getLigneCommandes()) {

                                if (ligne.getPrice() != null && ligne.getPrice().getIdPrice() != null) {

                                    String idPrice = ligne.getPrice().getIdPrice();
                                    int quantite = ligne.getQuantity();

                                    ventesParProduit.put(idPrice, ventesParProduit.getOrDefault(idPrice, 0) + quantite);

                                    detailsProduit.putIfAbsent(idPrice, ligne);
                                }
                            }
                        }
                    }
                }
            }
        }

        List<Map.Entry<String, Integer>> listeTriee = new ArrayList<>(ventesParProduit.entrySet());

        listeTriee.sort((p1, p2) -> p2.getValue().compareTo(p1.getValue()));

        List<TopProduit> top3 = new ArrayList<>();
        int limit = Math.min(3, listeTriee.size());

        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = listeTriee.get(i);

            String idPrice = entry.getKey();
            Integer quantiteVendue = entry.getValue();

            LigneCommande ligneRef = detailsProduit.get(idPrice);
            String nomProduit = ligneRef.getPrice().getProduct().getName();

            String imageUrl = ligneRef.getPrice().getProduct().getImageUrl();

            TopProduit tp = new TopProduit(
                    i + 1,
                    idPrice,
                    imageUrl,
                    nomProduit,
                    quantiteVendue,
                    "up"
            );
            top3.add(tp);
        }

        return top3;
    }

    private Reputation getReputation(Quincaillerie q) {

        // Tu prends directement les valeurs stockées dans la table Quincaillerie
        //Double noteMoyenne = q.getNoteMoyenne() != null ? q.getNoteMoyenne() : 0.0;
        //Integer nombreAvis = q.getNombreVotes() != null ? q.getNombreVotes() : 0;

        double noteMoyenne = 4.7;
        int nombreAvis = 500;

        return new Reputation(noteMoyenne, nombreAvis);
    }

    BigDecimal getFondEnAttente(List<Commande> commandes) {
        BigDecimal fond = BigDecimal.ZERO;

        for(Commande c : commandes) {
            if(c.getStatut() == StatutCommande.PAYEE && c.getMontantTotal() != null) {
                fond = fond.add(c.getMontantTotal());
            }
        }

        return fond;
    }
}
