package com.ict300.P04.Service.paiement;

import com.ict300.P04.DTO.paiement.request.ValidationRetraitDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.*;
import com.ict300.P04.Service.paiement.sharePayService.SharePayService;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.retraitCode.RetraitCodeInterface;
import com.ict300.P04.repository.interfaces.user.seller.SellerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RetraitService {

    @Autowired
    private RetraitCodeInterface retraitCodeInterface;

    @Autowired
    private DetailCommandeInterface detailCommandeInterface;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Autowired
    private SharePayService sharePayService;

    @Autowired
    private SellerInterface sellerInterface;

    @Autowired
    private RedixService redixService;

    @Transactional(noRollbackFor = {InvalidOtpCodeException.class, MaxAttemptsExceededException.class})
    public void validerRetrait(ValidationRetraitDTO requestBody, String ipVendeur, String userAgentVendeur, String quincaillerieId, String uid) {

        Quincaillerie quincaillerie = quincaillerieInterface.findById(quincaillerieId)
                .orElseThrow(() -> new ResourceNotFoundException("La quincaillerie n'existe pas"));

        User user = sellerInterface.findById(uid).orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas pour cette quincaillerie"));

        RetraitCode retraitCode = retraitCodeInterface.findByCommandeId(requestBody.getIdCommande())
                .orElseThrow(() -> new OtpCodeNotFoundException("Aucun code de retrait trouvé pour cette commande"));
        DetailCommande detailCommande = detailCommandeInterface.getDetailCommandeByCommande(retraitCode.getCommande().getIdCommande())
                .orElseThrow(() -> new ResourceNotFoundException("Détails de la commande introuvables"));

        Commande commande = retraitCode.getCommande();

        if (!commande.getQuincaillerie().getIdQuincaillerie().equals(quincaillerie.getIdQuincaillerie())) {
            throw new StoreMismatchException("Accès refusé : Cette commande n'appartient pas à votre quincaillerie.");
        }


        if (Instant.now().isAfter(retraitCode.getExpirationdate())) {
            throw new OtpCodeExpiredException("Le code a expiré, veuillez en demander un autre.");
        }


        if (retraitCode.getTentativesEchouees() >= 3) {
            throw new MaxAttemptsExceededException("Trop de tentatives échouées. Le code est bloqué.");
        }


        if (!passwordEncoder.matches(requestBody.getCodeSaisi(), retraitCode.getCodeHash())) {
            retraitCode.setTentativesEchouees(retraitCode.getTentativesEchouees() + 1);
            retraitCodeInterface.saveAndFlush(retraitCode);

            int restants = 3 - retraitCode.getTentativesEchouees();
            if (restants <= 0) {
                throw new MaxAttemptsExceededException("Dernière tentative échouée. Le code est désormais bloqué.");
            } else {
                throw new InvalidOtpCodeException("Code incorrect. Tentatives restantes : " + restants);
            }
        }

        redixService.saveContext(commande.getIdCommande(), ipVendeur, userAgentVendeur);

        retraitCode.setValid(false);
        retraitCodeInterface.save(retraitCode);

        sharePayService.transfererFondsAuVendeur(user.getPhone() , user.getName() , commande.getMontantTotal().doubleValue() , commande.getIdCommande() , user.getEmail());

    }
}