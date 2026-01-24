package com.ict300.P04.Controller.quincaillerie;

import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.DTO.recommadation.response.RecommendedProductDTO;
import com.ict300.P04.Service.quincaillerie.QuincaillerieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/quincaillerie/quincaillerie")
@Tag(name = "ManageQuincaillerie", description = "Gestion des quincaillerie")
public class QuincaillerieController {
    @Autowired
    private QuincaillerieService quincaillerieService;

    @Operation(summary = "Details quincaillerie", description = "details de la quincaillerie")
    @GetMapping("/details")
    public ResponseEntity<QuincaillerieDetailsDTO> getRecommendations(@RequestParam String idQuincaillerie) {

        QuincaillerieDetailsDTO detailQuincaillerie = quincaillerieService.getInfoQuincaillerei(idQuincaillerie);

        return ResponseEntity.ok(detailQuincaillerie);
    }
}
