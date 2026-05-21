package com.ict300.P04.Controller.paiement;

import com.ict300.P04.Service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quincaillerie")
public class TestController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/test-email")
    public String testerEmail() {
        try {
            String emailClient = "armel8594@gmail.com";
            String sujet = "✅ Reçu de paiement - Commande #125485";
            String montant = "10000";

            // Création du visuel HTML (Design de l'email)
            String contenuHtml = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>"
                    + "<h2 style='color: #D35400; text-align: center;'>Merci pour votre achat ! 🛠️</h2>"
                    + "<p style='font-size: 16px;'>Bonjour <strong>" + emailClient + "</strong>,</p>"
                    + "<p style='font-size: 16px;'>Nous vous confirmons la bonne réception de votre paiement d'un montant de <strong>"+montant+" FCFA</strong>.</p>"
                    + "<p style='font-size: 16px;'>Votre commande est en cours de préparation par notre équipe.</p>"
                    + "<div style='text-align: center; margin-top: 30px; margin-bottom: 30px;'>"
                    + "  <a href='" + emailClient + "' style='background-color: #D35400; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;'>📥 Télécharger ma facture</a>"
                    + "</div>"
                    + "<hr style='border: top 1px solid #eee;'>"
                    + "<p style='font-size: 14px; color: #777; text-align: center;'>L'équipe de Brixel<br>Merci de votre confiance.</p>"
                    + "</div>";

            // Appel de notre nouvelle méthode !
            emailService.envoyerEmailHtml(emailClient, sujet, contenuHtml);
            return "✅ Ordre d'envoi donné avec succès ! Vérifie ta boîte mail.";

        } catch (Exception e) {
            return "❌ Erreur lors de l'envoi : " + e.getMessage();
        }
    }
}
