package com.ict300.P04.Service.commmande;

import com.ict300.P04.DTO.commande.response.CommandeDetailDTO;
import com.ict300.P04.DTO.commande.response.CommandeResponseDTO;
import com.ict300.P04.DTO.commande.response.getCommandeDTO;
import com.ict300.P04.DTO.notification.SystemNotificationDTO;
import com.ict300.P04.DTO.paiement.redix.VendeurContextDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.*;
import com.ict300.P04.Service.email.EmailService;
import com.ict300.P04.Service.facture.FactureService;
import com.ict300.P04.Service.notification.SystemNotificationService;
import com.ict300.P04.Service.paiement.RedixService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.MouvementStock;
import com.ict300.P04.Utilitaires.NotificationType;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.detailRetrait.DetailRetraitInterface;
import com.ict300.P04.repository.interfaces.facture.FactureInterface;
import com.ict300.P04.repository.interfaces.ligneCommande.LigneCommandeInterface;
import com.ict300.P04.repository.interfaces.lignePanier.LignePanierInterface;
import com.ict300.P04.repository.interfaces.panier.PanierInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.stock.StockInterface;
import com.ict300.P04.repository.interfaces.systemNotification.SystemNotificationInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommandeService {

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private DetailRetraitInterface detailRetraitInterface;

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

    @Autowired
    private PriceInterface priceInterface;

    @Autowired
    private StockInterface stockInterface;

    @Autowired
    private SystemNotificationService notificationService;

    @Autowired
    private FactureService factureService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedixService redixService;

    @Autowired
    private SystemNotificationInterface systemNotificationInterface;

    @Autowired
    private FacturationAsyncService facturationAsyncService;

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
                CampagnePromotion campagnePromotion = ((CampagnePromotion) promoData[1]);

                double tauxRemise = 0.0;

                if(campagnePromotion != null && campagnePromotion.getTauxRemise() != null ){
                    tauxRemise = campagnePromotion.getTauxRemise().doubleValue();
                }

                double prixUnitaireBase = lignePanier.getPrice().getPrice().doubleValue();
                double prixFinalUnitaire = prixUnitaireBase * (1 - (tauxRemise / 100.0));

                totalDeCetteCommande += (prixFinalUnitaire * lignePanier.getQuantity());

                LigneCommande lc = new LigneCommande();

                lc.setIdLigneCommande(GenerateID.GenerateLigneCommandeID());
                lc.setPrice(lignePanier.getPrice());
                lc.setQuantity(lignePanier.getQuantity());
                lc.setPriceDeBase(BigDecimal.valueOf(prixUnitaireBase));
                lc.setPricePaye(BigDecimal.valueOf(prixFinalUnitaire));
                if(campagnePromotion != null){
                    lc.setCampagnePromotion(campagnePromotion);
                }

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
        User user = customerInterface.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("L'utilisateur n'existe pas"));

        List<Commande> commandes = commandeInterface.getAllCommandeByUser(userId);

        if(commandes.isEmpty()){
            return new ArrayList<>();
        }

        Map<String , DetailCommande> mapDetails = detailCommandeInterface.findByCommandeIn(commandes)
                .stream().collect(Collectors.toMap(d -> d.getCommande().getIdCommande() , d -> d));

        Map<String , Facture> mapFactures = factureInterface.findByCommandeIn(commandes)
                .stream().collect(Collectors.toMap(f -> f.getCommande().getIdCommande() , f-> f));

        List<getCommandeDTO> response = new ArrayList<>();


        for( Commande commande : commandes) {

            String idCmd = commande.getIdCommande();

            String url = mapFactures.containsKey(idCmd) ? mapFactures.get(idCmd).getUrlFacture() : null;

            LocalDateTime dateCommande = mapDetails.containsKey(idCmd) ? mapDetails.get(idCmd).getDateCommande() : null;

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
                 ligneCommande.getPricePaye().doubleValue(),
                 ligneCommande.getQuantity()
         )).toList();
    }

    public List<getCommandeDTO> getCommandeByQuincaillerie(String quincaillerieId) {
        Quincaillerie quincaillerie = quincaillerieInterface.findById(quincaillerieId)
                .orElseThrow(() -> new ResourceNotFoundException("La quincaillerie n'existe pas"));

        List<Commande> commandes = commandeInterface.getAllCommandeByQuincaillerie(quincaillerieId);

        if(commandes.isEmpty()){
            return new ArrayList<>();
        }

        Map<String , DetailCommande> mapDetails = detailCommandeInterface.findByCommandeIn(commandes)
                .stream().collect(Collectors.toMap(d -> d.getCommande().getIdCommande() , d -> d));

        Map<String , Facture> mapFactures = factureInterface.findByCommandeIn(commandes)
                .stream().collect(Collectors.toMap(f -> f.getCommande().getIdCommande() , f-> f));

        List<getCommandeDTO> response = new ArrayList<>();

        for( Commande commande : commandes) {

            String idCmd = commande.getIdCommande();

            String url = mapFactures.containsKey(idCmd) ? mapFactures.get(idCmd).getUrlFacture() : null;

            LocalDateTime dateCommande = mapDetails.containsKey(idCmd) ? mapDetails.get(idCmd).getDateCommande(): null;

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

     @Transactional
    public void confirmerPaiement(String idTransaction, String method) {
        Commande commande = commandeInterface.getCommandeByIdTransaction(idTransaction)
                .orElseThrow(() -> new ResourceNotFoundException("L'id de la transaction n'existe pas"));

        if(commande.getStatut() == StatutCommande.PAYEE) {
            log.info("La transaction {} a déjà été traitée. Webhook ignoré.", idTransaction);
            return;
        }

        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(commande.getIdCommande())
                .orElseThrow(() -> new ResourceNotFoundException("Les détails de cette commande sont introuvables."));

        List<LigneCommande> ligneCommandes = ligneCommandeInterface.getDetailCommande(commande.getIdCommande());

        LocalDateTime datePaiement = LocalDateTime.now();

        detailCommande.setDatePaiement(datePaiement);
        commande.setStatut(StatutCommande.PAYEE);

        detailCommandeInterface.save(detailCommande);
        commandeInterface.save(commande);

        List<Stock> stocks = new ArrayList<>();
        List<Price> prices = new ArrayList<>();
        List<SystemNotification> notificationsToSave = new ArrayList<>();


        for (LigneCommande lc : ligneCommandes) {
            Price price = priceInterface.findByIdPrice(lc.getPrice().getIdPrice())
                    .orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));


            if (price.getStock() < lc.getQuantity()) {
                log.error("URGENT: Le client a payé la commande {} mais le stock est insuffisant pour l'article {}", commande.getIdCommande(), price.getIdPrice());
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

            if(price.getStock() <= price.getStockSeuil()){

                SystemNotification systemNotification = new SystemNotification();

                systemNotification.setIdSystemNotification(GenerateID.GenerateSystemNotificationID());
                systemNotification.setType(NotificationType.STOCK_BAS);
                systemNotification.setMessage(String.format("Stock bas pour '%s' (%d %s restant)", price.getProduct().getName(), price.getStock(), price.getProduct().getUnit()));
                systemNotification.setQuincaillerie(commande.getQuincaillerie());
                systemNotification.setTargetId(price.getIdPrice());
                systemNotification.setIsRead(false);

                notificationsToSave.add(systemNotification);
            }

            prices.add(price);

        }

        stockInterface.saveAll(stocks);
        priceInterface.saveAll(prices);

        if(!notificationsToSave.isEmpty()){
            systemNotificationInterface.saveAll(notificationsToSave);

            List<SystemNotificationDTO> notificationDTOs = notificationsToSave.stream()
                    .map(notification -> new SystemNotificationDTO(
                            notification.getIdSystemNotification(),
                            notification.getMessage(),
                            notification.getType(),
                            notification.getTargetId(),
                            notification.getIsRead(),
                            notification.getCreatedAt()
                    )).toList();


            notificationService.broadcastNotifications(commande.getQuincaillerie().getIdQuincaillerie(), notificationDTOs);
        }


        BigDecimal tauxTva = new BigDecimal("0.1925");
        BigDecimal diviseurTtc = new BigDecimal("1.1925");

        BigDecimal totalTTC = commande.getMontantTotal();

        BigDecimal totalHT = totalTTC.divide(diviseurTtc, 2, RoundingMode.HALF_UP);

        BigDecimal totalTVA = totalTTC.subtract(totalHT);

        Facture facture = new Facture();

        facture.setCommande(commande);
        facture.setDateFacturation(LocalDateTime.now());
        facture.setIdFacture(GenerateID.GenerateFactureID());
        facture.setTotalHT(totalHT);
        facture.setTotalTVA(totalTVA);
        facture.setTotalTTC(totalTTC);
        facture.setModePaiement(method);

        facture = factureInterface.save(facture);

        facturationAsyncService.processFactureAndEmail(commande.getIdCommande() , facture.getIdFacture() , idTransaction , method);
    }

    @Transactional
    public void echecPaiement(String idTransaction) throws MessagingException {
        Commande commande = commandeInterface.getCommandeByIdTransaction(idTransaction)
                .orElseThrow(() -> new ResourceNotFoundException("L'id de la transaction n'existe pas"));

        if (commande.getStatut() == StatutCommande.PAYEE) {
            log.warn("ALERTE : Tentative de mise en échec d'une commande déjà PAYÉE (Transaction {}). Action bloquée.", idTransaction);
            return;
        }


        if (commande.getStatut() == StatutCommande.ANNULEE) {
            log.info("La commande {} est déjà traitée.", commande.getIdCommande());
            return;
        }


        String emailClient = commande.getUser().getEmail();
        String sujet = "⚠️ Problème technique avec votre paiement - Commande #" + commande.getIdCommande();

        String contenuHtml = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>"
                + "<h2 style='color: #E67E22; text-align: center;'>Zut ! Le paiement n'est pas passé ❌</h2>"
                + "<p style='font-size: 16px;'>Bonjour <strong>" + commande.getUser().getName() + "</strong>,</p>"
                + "<p style='font-size: 16px;'>Votre tentative de paiement pour la commande <strong>#" + commande.getIdCommande() + "</strong> a échoué (erreur réseau, validation expirée...).</p>"
                + "<div style='background-color: #FEF9E7; padding: 15px; border-left: 5px solid #E67E22; margin: 20px 0;'>"
                + "  <strong>Montant à régler :</strong> " + commande.getMontantTotal() + " FCFA"
                + "</div>"
                + "<p style='font-size: 16px;'><strong>Bonne nouvelle :</strong> Vos articles vous sont réservés pendant <strong>24 heures</strong> ! Passé ce délai, le stock sera libéré.</p>"
                + "<div style='text-align: center; margin-top: 30px; margin-bottom: 30px;'>"
                + "  <a href='https://ton-application-ou-site.com/reessayer' style='background-color: #E67E22; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;'>🔄 Réessayer le paiement immédiatement</a>"
                + "</div>"
                + "<hr style='border: 0; border-top: 1px solid #eee;'>"
                + "<p style='font-size: 14px; color: #777; text-align: center;'>L'équipe de Brixel</p>"
                + "</div>";

        emailService.envoyerEmailHtml(emailClient, sujet, contenuHtml);
        log.info("Email d'échec technique envoyé. Stock maintenu pour 24h.");
    }

    @Transactional
    public void annulerPaiement(String idTransaction) throws MessagingException {
        Commande commande = commandeInterface.getCommandeByIdTransaction(idTransaction)
                .orElseThrow(() -> new ResourceNotFoundException("L'id de la transaction n'existe pas"));

        if (commande.getStatut() == StatutCommande.PAYEE) {
            log.warn("ALERTE : Tentative d'annulation d'une commande déjà PAYÉE (Transaction {}). Action bloquée.", idTransaction);
            return;
        }

        if (commande.getStatut() == StatutCommande.ANNULEE) {
            log.info("La commande {} est déjà marquée comme annulée.", commande.getIdCommande());
            return;
        }


        commande.setStatut(StatutCommande.ANNULEE);
        commandeInterface.save(commande);

        String emailClient = commande.getUser().getEmail();
        String sujet = "Anulation de votre processus d'achat - Commande #" + commande.getIdCommande();

        String contenuHtml = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>"
                + "<h2 style='color: #7F8C8D; text-align: center;'>Paiement annulé 🛑</h2>"
                + "<p style='font-size: 16px;'>Bonjour <strong>" + commande.getUser().getName() + "</strong>,</p>"
                + "<p style='font-size: 16px;'>Vous avez choisi d'annuler votre paiement. Votre commande <strong>#" + commande.getIdCommande() + "</strong> a donc été fermée.</p>"
                + "<p style='font-size: 16px;'>Les articles ont été remis en stock pour les autres clients. Vous pouvez recréer un panier à tout moment.</p>"
                + "<div style='text-align: center; margin-top: 30px; margin-bottom: 30px;'>"
                + "  <a href='https://ton-application-ou-site.com/boutique' style='background-color: #2C3E50; color: white; padding: 15px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;'>🛒 Retourner à la boutique</a>"
                + "</div>"
                + "<hr style='border: 0; border-top: 1px solid #eee;'>"
                + "<p style='font-size: 14px; color: #777; text-align: center;'>L'équipe de Brixel</p>"
                + "</div>";

        emailService.envoyerEmailHtml(emailClient, sujet, contenuHtml);
        log.info("Commande {} annulée suite à l'action du client. Stock libéré.", commande.getIdCommande());
    }

    @Transactional
    public void confirmerTransfert(String idTransaction) {
        Commande commande = commandeInterface.getCommandeByIdTransaction(idTransaction)
                .orElseThrow(() -> new ResourceNotFoundException("L'id de la transaction n'existe pas"));

        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(commande.getIdCommande())
                .orElseThrow(() -> new ResourceNotFoundException("Détails de la commande introuvables"));

        commande.setStatut(StatutCommande.LIVREE);
        commandeInterface.save(commande);


        detailCommande.setDateRetrait(LocalDateTime.now());
        detailCommandeInterface.save(detailCommande);

        VendeurContextDTO vendeurContextDTO = redixService.getContext(commande.getIdCommande());

        String ip = "ip_inconnu";
        String userAgent = "user-egent_inconnu";

        if(vendeurContextDTO != null ){
            ip = vendeurContextDTO.getIpVendeur();
            userAgent = vendeurContextDTO.getUserAgentVendeur();
            redixService.deleteContext(commande.getIdCommande());
        }

        DetailRetrait preuve = new DetailRetrait();
        preuve.setIdDetailRetrait(GenerateID.GenerateDetailRetraitID());
        preuve.setCommande(commande);
        preuve.setIpVendeur(ip);
        preuve.setUserAgentVendeur(userAgent);
        detailRetraitInterface.save(preuve);

    }
}
