package com.ict300.P04.Service.localisation;

import com.ict300.P04.DTO.localisation.LocalisationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LocalisationService {

    @Autowired
    private RestTemplate restTemplate;

    public LocalisationDTO getAddress(double lat, double lng) {
        String url = String.format(
                "https://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s&zoom=18&addressdetails=1",
                lat, lng
        );


        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Brixel Backend");
        headers.set("Accept-Language", "fr");

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            LocalisationDTO dto = new LocalisationDTO();

            if (body != null && body.containsKey("address")) {
                Map<String, String> address = (Map<String, String>) body.get("address");


                dto.setRegion(address.getOrDefault("state", address.getOrDefault("region", "Non défini")));
                dto.setVille(address.getOrDefault("city", address.getOrDefault("town", address.getOrDefault("village", "Non défini"))));
                dto.setQuartier(address.getOrDefault("suburb", address.getOrDefault("neighbourhood", "Non défini")));
            } else {
                dto.setRegion("Non défini");
                dto.setVille("Non défini");
                dto.setQuartier("Non défini");
            }

            return dto;

        } catch (Exception e) {
            System.err.println("Erreur de géolocalisation: " + e.getMessage());


            LocalisationDTO errorDto = new LocalisationDTO();
            errorDto.setRegion("Non défini");
            errorDto.setVille("Non défini");
            errorDto.setQuartier("Non défini");

            return errorDto;
        }
    }
}