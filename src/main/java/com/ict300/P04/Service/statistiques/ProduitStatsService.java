package com.ict300.P04.Service.statistiques;

import com.ict300.P04.DTO.statistique.detailProduct.response.DernierMouvementDTO;
import com.ict300.P04.DTO.statistique.detailProduct.response.GraphiqueMouvementDTO;
import com.ict300.P04.DTO.statistique.detailProduct.response.IndicateursPerformanceDTO;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Stock;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Utilitaires.MouvementStock;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.stock.StockInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class ProduitStatsService {

    @Autowired
    private StockInterface stockInterface;

    @Autowired
    private PriceInterface priceInterface;

        /*
    public StockDashboardResponseDTO getDetailProduct(String idPrice, int jours) {
        Price price = priceInterface.findByIdPrice(idPrice).orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));

        List<GraphiqueMouvementDTO> graphiqueMouvementDTOList = getStatsProduct(idPrice , jours);
        IndicateursPerformanceDTO indicateursPerformanceDTO = getIndicateursProduct(idPrice , jours);
        DernierMouvementDTO dernierMouvementDTO = getLastMouvement(idPrice);

        return new StockDashboardResponseDTO(
                idPrice,
                price.getStock(),
                dernierMouvementDTO,
                indicateursPerformanceDTO,
                graphiqueMouvementDTOList
        ) ;
    }
     */

    public List<GraphiqueMouvementDTO> getStatsProduct(String idPrice, int jours) {
        LocalDateTime startdate = LocalDateTime.now().minusDays(jours);

        List<Stock> stocks = stockInterface.getStockProductByIdAndDate(idPrice, startdate);

        Map<LocalDateTime, List<Stock>> mouvementParTemps = new TreeMap<>();

        for (Stock s : stocks) {
            if (s.getDateMouvement() != null) {
                LocalDateTime dateMouvement = s.getDateMouvement();
                LocalDateTime cleGroupement;

                if (jours <= 1) {
                    cleGroupement = dateMouvement.truncatedTo(ChronoUnit.HOURS);
                } else {
                    cleGroupement = dateMouvement.truncatedTo(ChronoUnit.DAYS);
                }

                mouvementParTemps.computeIfAbsent(cleGroupement, k -> new ArrayList<>()).add(s);
            }
        }

        DateTimeFormatter displayFormatter;
        if (jours <= 1) {
            displayFormatter = DateTimeFormatter.ofPattern("HH'h'00");
        } else if (jours <= 7) {
            displayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.FRENCH);
        } else {
            displayFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.FRENCH);
        }


        List<GraphiqueMouvementDTO> listeStats = new ArrayList<>();

        for (Map.Entry<LocalDateTime, List<Stock>> entry : mouvementParTemps.entrySet()) {
            LocalDateTime temps = entry.getKey();
            List<Stock> mouvementsDuMoment = entry.getValue();

            int totalEntrees = 0;
            int totalSorties = 0;


            for (Stock s : mouvementsDuMoment) {
                if (s.getTypeMouvement() == MouvementStock.ENTREE) {
                    totalEntrees += s.getQuantity();
                } else if (s.getTypeMouvement() == MouvementStock.SORTIE) {
                    totalSorties += s.getQuantity();
                }
            }


            String dateDuJourText = temps.format(displayFormatter);
            if (!dateDuJourText.isEmpty()) {
                dateDuJourText = dateDuJourText.substring(0, 1).toUpperCase() + dateDuJourText.substring(1);
            }

            listeStats.add(new GraphiqueMouvementDTO(dateDuJourText, totalEntrees, totalSorties));
        }

        return listeStats;
    }

    public IndicateursPerformanceDTO getIndicateursProduct(String idPrice , int jours) {

        LocalDateTime dateLimite = LocalDateTime.now().minusDays(jours);
        log.error("l'id du suppose produit nont existant est "+idPrice);

        List<Stock> historique = stockInterface.findPriceByIdOrderByDateMouvementDesc(idPrice);
        Price price = priceInterface.findByIdPrice(idPrice).orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));


        int ventes30J = 0;
        int pertes30J = 0;
        LocalDateTime dernierReassort = null;

        for (Stock s : historique) {

            if (dernierReassort == null && s.getTypeMouvement() == MouvementStock.ENTREE) {
                dernierReassort = s.getDateMouvement();
            }

            if (s.getDateMouvement().isAfter(dateLimite) && s.getTypeMouvement() == MouvementStock.SORTIE) {

                String commentaire = s.getComment() != null ? s.getComment().toLowerCase() : "";

                if (commentaire.contains("perte") || commentaire.contains("casse")) {
                    pertes30J += s.getQuantity();
                } else {
                    ventes30J += s.getQuantity();
                }
            }

            if (dernierReassort != null && s.getDateMouvement().isBefore(dateLimite)) {
                break;
            }
        }

        double moyenneParSemaine = (ventes30J * 7.0) / Math.max(1 , jours);

        moyenneParSemaine = Math.round(moyenneParSemaine * 100.0) / 100.0;

        return new IndicateursPerformanceDTO(
                price.getStock(),
                price.getProduct().getName(),
                ventes30J,
                moyenneParSemaine,
                dernierReassort,
                pertes30J
        );
    }

    public DernierMouvementDTO getLastMouvement(String idPrice) {

        List<Stock> historique = stockInterface.findPriceByIdOrderByDateMouvementDesc(idPrice);

        if (historique == null || historique.isEmpty()) {
            return null;
        }


        Stock dernierStock = historique.get(0);


        return new DernierMouvementDTO(
                dernierStock.getDateMouvement(),
                dernierStock.getTypeMouvement(),
                dernierStock.getQuantity(),
                dernierStock.getComment()
        );
    }

}
