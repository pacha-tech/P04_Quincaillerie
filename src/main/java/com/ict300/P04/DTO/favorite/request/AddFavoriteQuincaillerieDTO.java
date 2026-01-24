package com.ict300.P04.DTO.favorite.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddFavoriteQuincaillerieDTO {
    @NotBlank
    private String idUser;

    @NotBlank
    private String idQuincaillerie;

    public AddFavoriteQuincaillerieDTO(String idUser , String idQuincaillerie){
        this.idUser = idUser;
        this.idQuincaillerie = idQuincaillerie;
    }

    public AddFavoriteQuincaillerieDTO(){}
}
