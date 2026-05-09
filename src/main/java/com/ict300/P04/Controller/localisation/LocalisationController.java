package com.ict300.P04.Controller.localisation;

import com.ict300.P04.DTO.localisation.LocalisationDTO;
import com.ict300.P04.Service.localisation.LocalisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quincaillerie/localisation")
public class LocalisationController {

    @Autowired
    private LocalisationService localisationService;

    @GetMapping
    public ResponseEntity<LocalisationDTO> getAddress(@RequestParam double lat , @RequestParam double lng) {
        LocalisationDTO result = localisationService.getAddress(lat,lng);
        return ResponseEntity.ok(result);
    }
}
