package com.ict300.P04.DTO.favorite.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddFavoriteProductDTO {
    @NotBlank
    private String idUser;

    @NotBlank
    private String idProduct;

    public AddFavoriteProductDTO(String idUser , String idProduct){
        this.idUser = idUser;
        this.idProduct = idProduct;
    }

    public AddFavoriteProductDTO(){}
}
