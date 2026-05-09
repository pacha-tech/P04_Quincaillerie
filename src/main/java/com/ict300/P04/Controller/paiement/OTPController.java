package com.ict300.P04.Controller.paiement;

import com.ict300.P04.DTO.paiement.request.ValidationRetraitDTO;
import com.ict300.P04.DTO.paiement.response.CodeOtpResponseDTO;
import com.ict300.P04.Exception.ApiError;
import com.ict300.P04.Service.paiement.OtpGenerationService;
import com.ict300.P04.Service.paiement.RetraitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/quincaillerie/otp")
@Tag(name = "ManageOTP", description = "Gestion des otp pour livrer les produits")
public class OTPController {

    @Autowired
    private RetraitService retraitService;

    @Autowired
    private OtpGenerationService otpGenerationService;

    @PostMapping("/valider")
    @Operation(summary = "validation d'un OTP par un vendeur")
    public ResponseEntity<?> validerRetrait(@Valid @RequestBody ValidationRetraitDTO requestBody, HttpServletRequest request , Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
        }

        String uid = authentication.getName();

        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) authentication.getDetails();
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Aucun détail d'authentification disponible"));
        }

        String quincaillerieId = (String) claims.get("quincaillerieId");
        if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "quincaillerieId manquant dans les claims"));
        }

        String userAgentVendeur = request.getHeader("User-Agent");
        if (userAgentVendeur == null) {
            userAgentVendeur = "Inconnu";
        }

        String ipVendeur = request.getHeader("X-Forwarded-For");
        if (ipVendeur == null || ipVendeur.isEmpty() || "unknown".equalsIgnoreCase(ipVendeur)) {
            ipVendeur = request.getRemoteAddr();
        } else {
            ipVendeur = ipVendeur.split(",")[0].trim();
        }

        retraitService.validerRetrait(requestBody , ipVendeur , userAgentVendeur , quincaillerieId);

        return ResponseEntity.ok("Code valide vous pouvez livrer la marchandise les fonds seront transferer des que possible");
    }

    @GetMapping("/getOtp/{id}")
    @Operation(summary = "Demande d'un OTP par un client")
    public ResponseEntity<?> getOtp(@PathVariable("id") String idCommande , Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié"));
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
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Pas de Role vous devez etre connectez"));
        }

        CodeOtpResponseDTO response = otpGenerationService.generateAndSaveOtp(idCommande , uid);

        return ResponseEntity.ok(response);
    }
}
