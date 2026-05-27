package com.ict300.P04.Service.paiement.aangaraPayService;

import com.ict300.P04.DTO.paiement.aangaraPay.request.RedirectPaymentRequest;
import com.ict300.P04.DTO.paiement.aangaraPay.response.PaymentData;
import com.ict300.P04.DTO.paiement.aangaraPay.response.PaymentInitiatedResponse;
import com.ict300.P04.DTO.paiement.sharePay.requete.SharePayTransferRequestDTO;
import com.ict300.P04.DTO.paiement.sharePay.response.SharePayResponseEnvelope;
import com.ict300.P04.DTO.paiement.sharePay.response.SharePayTransferResponseDTO;
import com.ict300.P04.Exception.PaymentGatewayException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.StatutPaiement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AangaraPayService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${aangara.api_key}")
    private String apiKey;

    private final String baseUrl = "https://api-production.aangaraa-pay.com/";


    public Map<String, String> initierPaiementCheckout(String idCommande, Double montantTotal, String operator) {

        String url = baseUrl + "/api/v1/redirect/payment";

        RedirectPaymentRequest requestBody = new RedirectPaymentRequest();

        requestBody.setAmount(montantTotal);
        requestBody.setDescription("Paiement de la commande #" + idCommande);
        requestBody.setApp_key(apiKey);
        requestBody.setTransaction_id(GenerateID.GenerateTransactionID());
        requestBody.setReturn_url("https://brixel-web.onrender.com/client/commande");
        requestBody.setNotify_url("https://brixel-web.onrender.com/client/commande");
        requestBody.setOperator(operator);
        requestBody.setDevise_id("XAF");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RedirectPaymentRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("AangaraaPay - Initiation de paiement pour la commande #{}", idCommande);

            ResponseEntity<PaymentInitiatedResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PaymentInitiatedResponse.class
            );

            PaymentInitiatedResponse apiResponse = response.getBody();

            if (apiResponse != null && apiResponse.getStatusCode() == 201 && apiResponse.getData() != null) {

                PaymentData data = apiResponse.getData();

                Map<String, String> result = new HashMap<>();
                result.put("paymentUrl", data.getPayment_url());
                result.put("transactionId", data.getTransaction_id());
                //result.put("paymentHistoryId", String.valueOf(data.getPaymentHistoryId()));
                result.put("message", apiResponse.getMessage());

                log.info("AangaraaPay - Paiement initié avec succès pour commande #{} | URL: {}", idCommande, data.getPayment_url());

                return result;
            } else {
                String errorMsg = apiResponse != null ? apiResponse.getMessage() : "Réponse vide de l'API";
                log.error("AangaraaPay - Erreur lors de l'initiation : {}", errorMsg);
                throw new PaymentGatewayException("Erreur AangaraaPay : " + errorMsg);
            }

        } catch (Exception e) {
            log.error("Échec de l'initiation de paiement AangaraaPay pour la commande {} : {}",
                    idCommande, e.getMessage(), e);
            throw new PaymentGatewayException("Impossible d'initier le paiement via AangaraaPay : " + e.getMessage());
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
            throw new PaymentGatewayException("Le transfert a échoué suite à un problème technique. Veuillez réessayer plus tard.");

        } catch (Exception e) {
            log.error("Erreur critique lors du transfert Pay-Out vers le vendeur : {}", e.getMessage());
            throw new PaymentGatewayException("Impossible de joindre le service de paiement.");
        }
    }

    public Map<String, String> initierPaiementDirect(String idCommande, BigDecimal amount, String operateur, String phoneNumber) {
        String url = baseUrl + "/api/v1/no_redirect/payment";

        Map<String, String> resultats = new HashMap<>();

        try {
            // 1. Formatage du numéro de téléphone (Ajout du 237 si absent)
            String formattedPhone = phoneNumber.trim();
            if (formattedPhone.length() == 9 && formattedPhone.startsWith("6")) {
                formattedPhone = "237" + formattedPhone;
            }
            System.out.println(formattedPhone);

            String idTransaction = GenerateID.GenerateTransactionID();

            // 3. Construction du corps de la requête (Payload)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("phone_number", formattedPhone);
            requestBody.put("amount" , amount);
            requestBody.put("description", "Paiement de la commande " + idCommande);
            requestBody.put("app_key", apiKey);
            requestBody.put("transaction_id", idTransaction);
            requestBody.put("return_url", "https://brixel-web.onrender.com/client/commande");
            requestBody.put("notify_url", "https://ventricle-frostlike-plethora.ngrok-free.dev/quincaillerie/paiement/webhook");
            requestBody.put("operator", operateur);
            requestBody.put("devise_id", "XAF");

            System.out.println(requestBody);

            // 4. Configuration des en-têtes HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 5. Appel à l'API AangaraaPay
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            // 6. Traitement de la réponse
            if (response.getStatusCode() == HttpStatus.CREATED && responseBody != null) {

                // AangaraaPay renvoie les infos dans un sous-objet "data"
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

                if (data != null && data.containsKey("payToken")) {
                    resultats.put("payToken", (String) data.get("payToken"));
                    resultats.put("transactionId", idTransaction);
                    resultats.put("message", (String) responseBody.get("message"));
                    return resultats;
                } else {
                    throw new RuntimeException("L'API n'a pas renvoyé de payToken.");
                }

            } else {
                String errorMsg = responseBody != null ? (String) responseBody.get("message") : "Erreur inconnue";
                throw new RuntimeException("Erreur AangaraaPay : " + errorMsg);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'initiation du paiement direct : " + e.getMessage());
            throw new RuntimeException("Impossible d'initier le paiement sur le téléphone. Veuillez réessayer.");
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

    public StatutPaiement verifierStatutDepot(String payToken) {
        String url = baseUrl + "/api/v1/aangaraa_check_status";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("payToken", payToken);
        requestBody.put("app_key", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            log.info("AangaraaPay - Vérification du statut du dépôt pour le payToken: {}", payToken);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
                StatutPaiement status = (StatutPaiement) responseBody.get("status");
                log.info("AangaraaPay - Statut dépôt récupéré officiellement : {}", status);
                return status != null ? status : StatutPaiement.PENDING;
            }

        } catch (Exception e) {
            log.error("AangaraaPay - Échec de l'appel de vérification du dépôt (payToken: {}) : {}", payToken, e.getMessage());
        }

        return StatutPaiement.PENDING;
    }

    public StatutPaiement verifierStatutRetrait(String transactionId, String operator) {
        String validatedOperator = "Orange_Cameroon";
        if (operator != null) {
            if (operator.toUpperCase().contains("MTN")) {
                validatedOperator = "MTN_Cameroon";
            } else if (operator.toUpperCase().contains("ORANGE")) {
                validatedOperator = "Orange_Cameroon";
            }
        }

        String url = baseUrl + "/api/v1/check_withdrawal_status/" + transactionId + "?payment_method=" + validatedOperator;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            log.info("AangaraaPay - Vérification du statut du retrait pour l'ID: {} via {}", transactionId, validatedOperator);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK && responseBody != null) {
                StatutPaiement status = (StatutPaiement) responseBody.get("status");
                log.info("AangaraaPay - Statut retrait récupéré officiellement : {}", status);
                return status != null ? status : StatutPaiement.PENDING;
            }

        } catch (Exception e) {
            log.error("AangaraaPay - Échec de l'appel de vérification du retrait (ID: {}) : {}", transactionId, e.getMessage());
        }

        return StatutPaiement.PENDING;
    }
}
