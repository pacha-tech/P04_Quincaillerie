package com.ict300.P04.DTO.paiement.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ValidationRetraitDTO {
    @NotBlank(message = "l'id de la commande est obligatoire")
    private String idCommande;

    @NotBlank(message = "Le code de retrait est obligatoire")
    @Pattern(regexp = "^\\d{6}$", message = "Le code doit être composé d'exactement 6 chiffres")
    private String codeSaisi;
}
