package com.ict300.P04.Controller.paiement;

import com.ict300.P04.DTO.paiement.response.PaiementResponseDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Service.paiement.PaiementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@RestController
@RequestMapping("/quincaillerie/paiement")
@Tag(name = "ManagePaiement", description = "Gestion des paiements")
@Slf4j
public class PaiementController {

    @Autowired
    private PaiementService paymentService;

    @Value("${sharepay.webhook_secret}")
    private String webhookSecret;

    @Operation(summary = "Paiement d'une commande par un client connecté")
    @PostMapping("/pay/{id}")
    public ResponseEntity<?> simulatePayment(@PathVariable("id") String idCommande, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
        }

        String uid = authentication.getName();

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Aucun détail d'authentification disponible"));
        }

        String role = (String) claims.get("role");
        if (role == null || role.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Pas de Role vous devez être connecté"));
        }

        PaiementResponseDTO responseDTO = paymentService.processPayment(idCommande, uid);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestHeader(value = "X-Sharepay-Signature", required = false) String signature,
            @RequestBody byte[] rawBody) {

        if (signature == null || !isValidSignature(rawBody, signature)) {
            log.warn("Appel webhook rejeté : signature invalide ou absente.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Signature invalide");
        }

        try {
            // Conversion en String sécurisée une fois la signature validée
            String jsonPayload = new String(rawBody, StandardCharsets.UTF_8);

            log.info("Webhook SharePay authentifié. Traitement de l'événement...");
            // Ton service gère maintenant le parsing JSON et la logique métier
            paymentService.handleWebhook(jsonPayload);

            return ResponseEntity.ok("OK"); // Obligatoire pour dire à SharePay d'arrêter d'envoyer le webhook
        } catch (Exception e) {
            log.error("Erreur lors du traitement du webhook : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur de traitement");
        }
    }

    private boolean isValidSignature(byte[] rawBody, String signatureHeader) {
        try {
            // 1. Initialisation de l'algorithme HMAC SHA256
            SecretKeySpec signingKey = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            // 2. Hashage direct du flux binaire
            byte[] rawHmac = mac.doFinal(rawBody);
            String actualSignature = HexFormat.of().formatHex(rawHmac);

            // 3. Extraction de la signature SharePay (Format "t=...,v1=...")
            String extractedSignature = signatureHeader;
            if (signatureHeader.contains("v1=")) {
                String[] parts = signatureHeader.split(",");
                for (String part : parts) {
                    if (part.trim().startsWith("v1=")) {
                        extractedSignature = part.trim().substring(3);
                        break;
                    }
                }
            }

            // 4. Comparaison sécurisée insensible à la casse
            return MessageDigest.isEqual(
                    actualSignature.toLowerCase().getBytes(StandardCharsets.UTF_8),
                    extractedSignature.toLowerCase().getBytes(StandardCharsets.UTF_8)
            );

        } catch (Exception e) {
            log.error("Erreur technique de validation de la signature : {}", e.getMessage());
            return false;
        }
    }
}