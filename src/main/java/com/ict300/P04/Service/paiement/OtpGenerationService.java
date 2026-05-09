package com.ict300.P04.Service.paiement;

import com.ict300.P04.DTO.paiement.response.CodeOtpResponseDTO;
import com.ict300.P04.Entite.Commande;
import com.ict300.P04.Entite.RetraitCode;
import com.ict300.P04.Exception.CommandeAlreadyCancelledException;
import com.ict300.P04.Exception.OrderNotFoundException;
import com.ict300.P04.Exception.OrderNotPaidException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.StatutCommande;
import com.ict300.P04.repository.interfaces.commande.CommandeInterface;
import com.ict300.P04.repository.interfaces.retraitCode.RetraitCodeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpGenerationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RetraitCodeInterface retraitCodeInterface;

    @Autowired
    private CommandeInterface commandeInterface;



    @Transactional
    public CodeOtpResponseDTO generateAndSaveOtp(String idCommande , String uid) {

        Commande commande = commandeInterface.findById(idCommande)
                .orElseThrow(() -> new OrderNotFoundException("La commande n'existe pas"));

        if(StatutCommande.ANNULEE.equals(commande.getStatut())){
            throw new CommandeAlreadyCancelledException("La commande est annulée");
        }

        if(!StatutCommande.PAYEE.equals(commande.getStatut())){
            throw new OrderNotPaidException("La commande n'est pas encore payée");
        }

        if(!commande.getUser().getIdUser().equals(uid)){
            throw new UserNotFoundException("Cette Commande ne vous appartient pas");
        }

        String codeEnClair = genererCodeSixChiffres();
        String codeHashe = passwordEncoder.encode(codeEnClair);

        LocalDateTime dateCreation = LocalDateTime.now();
        LocalDateTime dateExpiration = dateCreation.plusMinutes(5);


        RetraitCode retraitCode = retraitCodeInterface.findByCommandeId(idCommande).orElseGet(() -> {
            RetraitCode newCode = new RetraitCode();
            newCode.setIdRetraitCode(GenerateID.GenerateRetraitCodeID());
            return newCode;
        });

        retraitCode.setCommande(commande);
        retraitCode.setCodeHash(codeHashe);
        retraitCode.setExpirationdate(dateExpiration);
        retraitCode.setTentativesEchouees(0);
        retraitCode.setValid(true);

        retraitCodeInterface.save(retraitCode);


        return new CodeOtpResponseDTO(codeEnClair, dateExpiration, dateCreation);
    }

    private String genererCodeSixChiffres() {
        SecureRandom random = new SecureRandom();

        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }
}
