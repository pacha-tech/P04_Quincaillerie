package com.ict300.P04.DTO.favorite.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteFavoriteQuincaillerieDTO {
    @NotBlank
    private String idUser;

    @NotBlank
    private String idQuincaillerie;

    public DeleteFavoriteQuincaillerieDTO(String idUser , String idQuincaillerie){
        this.idUser = idUser;
        this.idQuincaillerie = idQuincaillerie;
    }
}
