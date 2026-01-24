package com.ict300.P04.DTO.favorite.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteFavoriteProductDTO {
    @NotBlank
    private String idUser;

    @NotBlank
    private String idProduct;

    public DeleteFavoriteProductDTO(String idUser , String idProduct){
        this.idUser = idUser;
        this.idProduct = idProduct;
    }
}
