package com.ict300.P04.DTO.category.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCategoryDTO {
    @NotBlank(message = "le nom de la categorie est obligatoire")
    private String name;

    @NotBlank(message = "Ajoute une description a la categorie")
    private String description;
}
