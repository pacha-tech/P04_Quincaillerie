package com.ict300.P04.Controller.user.seller;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.quincaillerie.response.ComptePaiementDTO;
import com.ict300.P04.Service.quincaillerie.ComptePaiementVendeurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quincaillerie")
@Tag(name = "InfosSeller", description = "La modifications des infos du vendeur ou il recevra l'argent")
public class InfosPaiementVendeur {

    @Autowired
    private ComptePaiementVendeurService comptePaiementVendeurService;

    @GetMapping("/getDetailVendeur")
    @Operation(summary = "Obtenir les informations de paiement", description = "Récupère le compte de paiement Mobile Money associé à la quincaillerie. Initialise un compte par défaut si aucun n'existe.")
    public ResponseEntity<?> getConfigurationPaiement(Authentication authentication) {

        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);

        ComptePaiementDTO config = comptePaiementVendeurService.getConfigurationPaiement(quincaillerieId);
        return ResponseEntity.ok(config);
    }


    @PutMapping("/modifyDetailVendeur")
    @Operation(summary = "Modifier les informations de paiement", description = "Met à jour le nom du compte et le numéro de téléphone. L'opérateur (MTN/Orange) est détecté automatiquement.")
    public ResponseEntity<?> modifierConfigurationPaiement(@RequestBody ComptePaiementDTO comptePaiementDTO , Authentication authentication) {

        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);

        ComptePaiementDTO updatedConfig = comptePaiementVendeurService.modifierConfigurationPaiement(quincaillerieId, comptePaiementDTO);
        return ResponseEntity.ok(updatedConfig);
    }
}