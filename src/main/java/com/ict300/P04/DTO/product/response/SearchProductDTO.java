package com.ict300.P04.DTO.product.response;

import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import lombok.Data;

import java.util.List;

@Data
public class SearchProductDTO {
    private String name;
    private List<PriceSearchProductDTO> priceSearchProductsDTO;
    private String description;

    public SearchProductDTO(String name , List<PriceSearchProductDTO> priceSearchProductsDTO , String description){
        this.name = name;
        this.priceSearchProductsDTO = priceSearchProductsDTO;
        this.description = description;
    }

    public SearchProductDTO(){}
}
