package com.ict300.P04.Service.paiement.sharePayService;

import com.ict300.P04.DTO.paiement.sharePay.requete.InitialPaiementRequestDTO;
import com.ict300.P04.DTO.paiement.sharePay.requete.SharePayTransferRequestDTO;
import com.ict300.P04.DTO.paiement.sharePay.response.SharePayCheckoutData;
import com.ict300.P04.DTO.paiement.sharePay.response.SharePayResponseEnvelope;
import com.ict300.P04.DTO.paiement.sharePay.response.SharePayTransferResponseDTO;
import com.ict300.P04.Exception.PaymentGatewayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SharePayService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${sharepay.api_key}")
    private String apiKey;

    private final String baseUrl = "https://sharepay-api.te-sea.com/";


    public Map<String, String> initierPaiementCheckout(String idCommande, Double montantTotal) {
        String url = baseUrl + "/api/v1/pay-in/checkout";


        InitialPaiementRequestDTO requestBody = new InitialPaiementRequestDTO(
                montantTotal.intValue(),
                "XAF",
                idCommande,
                "Paiement de la commande #" + idCommande,
                "https://brixel-web.onrender.com/client/commande",
                "https://brixel-web.onrender.com/client/commande"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InitialPaiementRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("SharePay - Création session checkout pour la commande #{}", idCommande);


            ResponseEntity<SharePayResponseEnvelope<SharePayCheckoutData>> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            SharePayResponseEnvelope<SharePayCheckoutData> envelope = response.getBody();

            if (envelope != null && envelope.isSuccess() && envelope.getData() != null) {
                SharePayCheckoutData data = envelope.getData();
                Map<String, String> result = new HashMap<>();
                result.put("paymentUrl", data.getPaymentUrl());
                result.put("reference", data.getReference());
                result.put("message", envelope.getMessage());
                return result;
            } else {
                String errorMsg = envelope != null ? envelope.getMessage() : "Réponse de l'agrégateur vide";
                throw new PaymentGatewayException("Erreur SharePay : " + errorMsg);
            }
        } catch (Exception e) {
            log.error("Échec du Pay-In Checkout SharePay pour la commande {} : {}", idCommande, e.getMessage());
            throw new PaymentGatewayException("Impossible d'initier le paiement : " + e.getMessage());
        }
    }


    public void transfererFondsAuVendeur(String telephoneVendeur, String nomVendeur, double montant, String idCommande , String emailVendeur) {
        String url = baseUrl + "/api/v1/pay-out/transfer";


        String provider = detecterProviderMobileMoney(telephoneVendeur);


        SharePayTransferRequestDTO requestBody = new SharePayTransferRequestDTO(
                (int) montant,
                "XAF",
                provider,
                telephoneVendeur,
                nomVendeur,
                idCommande,
                "Virement Brixel - Fin de commande #" + idCommande,
                emailVendeur
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SharePayTransferRequestDTO> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("SharePay - Lancement du Pay-Out de {} XAF vers le compte {}", montant, telephoneVendeur);


            ResponseEntity<SharePayResponseEnvelope<SharePayTransferResponseDTO>> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<>() {
                    }
            );

            SharePayResponseEnvelope<SharePayTransferResponseDTO> envelope = response.getBody();

            if (envelope != null && envelope.isSuccess()) {
                log.info("Pay-Out initié avec succès pour la commande #{}. Référence : {}",
                        idCommande, envelope.getData().getReference());
                return;
            }

            String errorMsg = envelope != null ? envelope.getMessage() : "Réponse vide";
            log.error("SharePay a refusé la demande de transfert. Raison : {}", errorMsg);
            return;

        } catch (Exception e) {
            log.error("Erreur critique lors du transfert Pay-Out vers le vendeur : {}", e.getMessage());
            throw new PaymentGatewayException("Échec technique du virement sortant via SharePay : " + e.getMessage());
        }
    }

    public String detecterProviderMobileMoney(String telephone) {
        if (telephone == null || telephone.isBlank()) {
            throw new PaymentGatewayException("Le numéro de téléphone est vide.");
        }

        // 1. Nettoyage : on retire les espaces, tirets et le "+"
        String numeroPropre = telephone.replaceAll("[\\s\\-+]", "");

        // 2. On s'assure qu'on a juste les 9 derniers chiffres si le 237 est présent
        if (numeroPropre.startsWith("237") && numeroPropre.length() == 12) {
            numeroPropre = numeroPropre.substring(3); // Garde uniquement "6..."
        }

        // 3. Validation de la longueur (Un numéro camerounais fait 9 chiffres et commence par 6)
        if (numeroPropre.length() != 9 || !numeroPropre.startsWith("6")) {
            throw new PaymentGatewayException("Format de numéro invalide pour le Cameroun : " + telephone);
        }

        // 4. Détection précise avec Regex
        // MTN : 67x, 68x, et 650 à 654
        if (numeroPropre.matches("^6(7[0-9]|8[0-9]|5[0-4])[0-9]{6}$")) {
            return "MTN_MOMO_CM";
        }

        // Orange : 69x, et 655 à 659
        if (numeroPropre.matches("^6(9[0-9]|5[5-9])[0-9]{6}$")) {
            return "ORANGE_MONEY_CM";
        }

        // Si c'est Nexttel (66) ou Camtel (62), on bloque car ils n'ont pas d'API Mobile Money standardisée ici
        throw new PaymentGatewayException("Cet opérateur n'est pas pris en charge pour le retrait : " + telephone);
    }
}