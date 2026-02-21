package com.ict300.P04.DTO.quincaillerie.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterQuincaillerieDTO {
    private String idUser;

    @NotBlank(message = "Le Nom est obligatoire")
    private String storeName;

    private String region;
    private String ville;
    private String quartier;
    private String precision;
    private String photoUrl;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private String nui;
    private boolean acceptTerms;
    private boolean wantTips;

    public RegisterQuincaillerieDTO(String idUser , String storeName , String region , String ville , String quartier , String precision , String photoUrl , String description , BigDecimal latitude , BigDecimal longitude , String phone ,String nui , boolean acceptTerms , boolean wantTips) {
        this.idUser = idUser;
        this.storeName = storeName;
        this.region = region;
        this.ville = ville;
        this.quartier = quartier;
        this.precision = precision;
        this.description = description;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.nui = nui;
        this.acceptTerms = acceptTerms;
        this.wantTips = wantTips;
    }

    public RegisterQuincaillerieDTO(){}
}
