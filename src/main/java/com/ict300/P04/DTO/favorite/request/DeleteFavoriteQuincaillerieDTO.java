package com.ict300.P04.DTO.favorite.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @AllArgsConstructor @NoArgsConstructor
public class DeleteFavoriteQuincaillerieDTO {
    @NotBlank
    private String idUser;

    @NotBlank
    private String idQuincaillerie;
}
