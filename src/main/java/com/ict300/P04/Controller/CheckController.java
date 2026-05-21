package com.ict300.P04.Controller; // Tu pourras le déplacer dans .Security ou .Util plus tard

import com.ict300.P04.Exception.ApiError; // Ou tes exceptions customs
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.Map;
import java.util.Optional;

public class CheckController {

    /**
     * Valide l'authentification globale (valable pour TOUT LE MONDE : Client et Vendeur)
     */
    public static Optional<ResponseEntity<ApiError>> validateBasicAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifié")));
        }
        return Optional.empty();
    }

    /**
     * Valide spécifiquement qu'il s'agit d'une Quincaillerie (Vendeur)
     */
    public static Optional<ResponseEntity<ApiError>> validateQuincaillerieAuthentication(Authentication authentication) {
        // 1. Vérification de base
        var basicCheck = validateBasicAuthentication(authentication);
        if (basicCheck.isPresent()) return basicCheck;

        // 2. Vérification des claims spécifiques au vendeur
        Map<String, Object> claims = getClaims(authentication);
        if (claims == null) {
            return Optional.of(ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Aucun détail d'authentification disponible")));
        }

        String quincaillerieId = (String) claims.get("quincaillerieId");
        if (quincaillerieId == null || quincaillerieId.trim().isEmpty()) {
            return Optional.of(ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiError(HttpStatus.FORBIDDEN, "Accès refusé : quincaillerieId manquant dans les jetons")));
        }

        return Optional.empty();
    }

    // --- Les Extracteurs de données ---

    public static String getUserId(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }

    public static String getQuincaillerieId(Authentication authentication) {
        Map<String, Object> claims = getClaims(authentication);
        return claims != null ? (String) claims.get("quincaillerieId") : null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getClaims(Authentication authentication) {
        if (authentication == null) return null;
        return (Map<String, Object>) authentication.getDetails();
    }
}