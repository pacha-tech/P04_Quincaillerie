package com.ict300.P04.DTO.product.response;

import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import lombok.Data;
import java.util.List;

@Data
public class SearchProductDTO {
    private String idProduct;
    private String idCategory;
    private String name;
    private String unite;
    private List<PriceSearchProductDTO> priceSearchProductsDTO;
    private String description;

    public SearchProductDTO(String idProduct , String idCategory , String name , String unite , List<PriceSearchProductDTO> priceSearchProductsDTO , String description){
        this.idProduct = idProduct;
        this.idCategory = idCategory;
        this.name = name;
        this.unite = unite;
        this.priceSearchProductsDTO = priceSearchProductsDTO;
        this.description = description;
    }

    public SearchProductDTO(){}
}
