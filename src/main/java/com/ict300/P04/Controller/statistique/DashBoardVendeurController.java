package com.ict300.P04.Controller.statistique;

import com.ict300.P04.Controller.CheckController;
import com.ict300.P04.DTO.quincaillerie.dashboard.response.Dashboard;
import com.ict300.P04.Service.vente.DashboardVendeurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/quincaillerie")
@Tag(name = "ManageDashBoard", description = "Gestion du dashBoard")
public class DashBoardVendeurController {

    @Autowired
    DashboardVendeurService dashboardVendeurService;

    @GetMapping("/getDashboard")
    @Operation(summary = "Obtenir les donnees du dashboard du vendeur")
    public ResponseEntity<?> getDashboardData(@RequestParam int period , Authentication authentication) {

        var errorResponse = CheckController.validateQuincaillerieAuthentication(authentication);
        if (errorResponse.isPresent()) return errorResponse.get();

        String quincaillerieId = CheckController.getQuincaillerieId(authentication);

        Dashboard dashboard = dashboardVendeurService.getDashboardVendeur(quincaillerieId, period);

        System.out.println(dashboard);

        return ResponseEntity.ok(dashboard);
    }
}
