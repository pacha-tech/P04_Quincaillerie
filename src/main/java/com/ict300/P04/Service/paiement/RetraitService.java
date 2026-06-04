package com.ict300.P04.Service.paiement;

import com.ict300.P04.DTO.paiement.aangaraPay.response.WithdrawalResponse;
import com.ict300.P04.DTO.paiement.request.ValidationRetraitDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.*;
import com.ict300.P04.Service.paiement.aangaraPayService.AangaraPayService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.Utilitaires.StatutPaiement;
import com.ict300.P04.repository.interfaces.detailCommande.DetailCommandeInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.retraitCode.RetraitCodeInterface;
import com.ict300.P04.repository.interfaces.transaction.versement.TransactionVersementInterface;
import com.ict300.P04.repository.interfaces.user.seller.SellerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

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
    private AangaraPayService aangaraPayService;

    @Autowired
    private TransactionVersementInterface transactionVersementInterface;

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

        boolean dejaPaye = commande.getStatut().equals(StatutCommande.LIVREE);

        if (dejaPaye) {
            throw new RuntimeException("Les fonds pour cette commande ont déjà été transférés au vendeur.");
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

        String amount = String.valueOf(commande.getMontantTotal().longValue());
        WithdrawalResponse withdrawalResponse = aangaraPayService.transfererFondsAuVendeur(commande.getQuincaillerie().getIdQuincaillerie() , amount);

        System.out.println(withdrawalResponse);

        TransactionVersement versement = new TransactionVersement();

        versement.setIdTransactionVersement(GenerateID.GenerateTransactionVersementID());
        versement.setReferenceId(withdrawalResponse.getData().getReference_id());
        versement.setIdTransaction(withdrawalResponse.getData().getTransaction_id());
        versement.setMontantNetTransfere(Double.parseDouble(amount));
        versement.setNumeroTelephone(withdrawalResponse.getData().getPhone_number());
        versement.setOperateur(withdrawalResponse.getData().getPayment_method());
        versement.setStatut(StatutPaiement.PENDING);
        versement.setCommande(commande);
        versement.setDateCreation(LocalDateTime.now());
        versement.setDateMiseAJour(LocalDateTime.now());

        transactionVersementInterface.save(versement);
    }
}