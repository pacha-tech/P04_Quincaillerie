package com.ict300.P04.DTO.product.response;

import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data  @AllArgsConstructor @NoArgsConstructor
public class SearchProductDTO {
    private String idProduct;
    private String idCategory;
    private String name;
    private String unite;
    private List<PriceSearchProductDTO> priceSearchProductsDTO;
    private String description;

}
