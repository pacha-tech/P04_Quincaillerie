package com.ict300.P04.Service.commmande;

import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.Facture;
import com.ict300.P04.Service.email.EmailService;
import com.ict300.P04.Service.facture.FactureService;
import com.ict300.P04.repository.interfaces.facture.FactureInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturationAsyncService {

    private final FactureService factureService;
    private final EmailService emailService;
    private final FactureInterface factureInterface;

    @Async
    public void processFactureAndEmail(Commande commande, Facture facture, String idTransaction, String method) {
        log.info("Début du traitement asynchrone (Génération PDF & Email) pour la commande {}", commande.getIdCommande());

        try {

            String urlCloudinary = factureService.generateFacture(commande, commande.getUser(), idTransaction, method);

            facture.setUrlFacture(urlCloudinary);
            factureInterface.save(facture);


            String emailClient = commande.getUser().getEmail();
            String sujet = "✅ Reçu de paiement - Commande #" + commande.getIdCommande();

            String contenuHtml = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #D35400; text-align: center;'>Merci pour votre achat ! 🛒</h2>"
                    + "<p style='font-size: 16px;'>Bonjour <strong>" + commande.getUser().getName() + "</strong>,</p>"
                    + "<p style='font-size: 16px;'>Nous vous confirmons la bonne réception de votre paiement d'un montant de <strong>" + facture.getTotalTTC() + " FCFA</strong>.</p>"
                    + "<p style='font-size: 16px;'>Votre commande est en cours de préparation par notre équipe.</p>"
                    + "<div style='text-align: center; margin-top: 30px; margin-bottom: 30px;'>"
                    + "  <a href='" + urlCloudinary + "' style='background-color: #D35400; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;'>⬇️ Télécharger ma facture</a>"
                    + "</div>"
                    + "<hr style='border: top 1px solid #eee;'>"
                    + "<p style='font-size: 14px; color: #777; text-align: center;'>L'équipe de Brixel<br>Merci de votre confiance.</p>"
                    + "</div>";

            emailService.envoyerEmailHtml(emailClient, sujet, contenuHtml);
            log.info("Facture HTML envoyée avec succès au client {}", emailClient);

        } catch (Exception e) {
            log.error("Erreur critique en arrière-plan lors de la facturation/email pour la commande [{}] : {}", commande.getIdCommande(), e.getMessage(), e);
        }
    }
}
