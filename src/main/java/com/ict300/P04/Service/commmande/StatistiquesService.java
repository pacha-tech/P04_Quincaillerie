package com.ict300.P04.Service.commmande;

import com.ict300.P04.DTO.commande.response.CommandeStatsDTO;
import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Service
public class StatistiquesService {

    @Autowired
    private CommandeInterface commandeInterface;

    public List<CommandeStatsDTO> getStatsForChart(String idQuincaillerie, int jourEnArriere) {
        LocalDateTime startdate = LocalDateTime.now().minusDays(jourEnArriere);
        List<Commande> commandes = commandeInterface.getCommandesByQuincaillerieAndDate(idQuincaillerie, startdate);


        Map<LocalDateTime, List<Commande>> commandesParTemps = new TreeMap<>();

        for (Commande c : commandes) {
            if (c.getDetailCommande() != null && c.getDetailCommande().getDateCommande() != null) {
                LocalDateTime dateComplete = c.getDetailCommande().getDateCommande();
                LocalDateTime cleGroupement;

                if (jourEnArriere <= 1) {
                    cleGroupement = dateComplete.truncatedTo(ChronoUnit.HOURS);
                }

                else {
                    cleGroupement = dateComplete.truncatedTo(ChronoUnit.DAYS);
                }

                commandesParTemps.computeIfAbsent(cleGroupement, k -> new ArrayList<>()).add(c);
            }
        }


        DateTimeFormatter displayFormatter;
        if (jourEnArriere <= 1) {
            displayFormatter = DateTimeFormatter.ofPattern("HH'h'00");
        } else if (jourEnArriere <= 7) {
            displayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.FRENCH);
        } else {
            displayFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.FRENCH);
        }

        List<CommandeStatsDTO> listeStats = new ArrayList<>();

        for (Map.Entry<LocalDateTime, List<Commande>> entry : commandesParTemps.entrySet()) {
            LocalDateTime temps = entry.getKey();
            List<Commande> commandesDuMoment = entry.getValue();

            long countPayee = 0;
            long countLivree = 0;
            long countAnnulee = 0;
            long countA_payee = 0;
            long countA_valider = 0;

            for (Commande c : commandesDuMoment) {
                if (c.getStatut() == StatutCommande.PAYEE) {
                    countPayee++;
                } else if (c.getStatut() == StatutCommande.LIVREE) {
                    countLivree++;
                } else if (c.getStatut() == StatutCommande.ANNULEE) {
                    countAnnulee++;
                } else if (c.getStatut() == StatutCommande.EN_ATTENTE_PAIEMENT) {
                    countA_payee++;
                } else if (c.getStatut() == StatutCommande.EN_ATTENTE_VALIDATION) {
                    countA_valider++;
                }
            }


            String dateDuJourText = temps.format(displayFormatter);


            if (!dateDuJourText.isEmpty()) {
                dateDuJourText = dateDuJourText.substring(0, 1).toUpperCase() + dateDuJourText.substring(1);
            }

            listeStats.add(new CommandeStatsDTO(dateDuJourText, countPayee, countLivree, countAnnulee , countA_valider , countA_payee));
        }

        return listeStats;
    }
}