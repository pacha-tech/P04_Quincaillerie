package com.ict300.P04.DTO.localisation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class LocalisationDTO {
    private String region;
    private String ville;
    private  String quartier;
}
