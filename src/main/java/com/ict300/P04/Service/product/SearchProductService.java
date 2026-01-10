package com.ict300.P04.Service.product;

import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchProductService {
    @Autowired
    private ProductInterface productInterface;

    public List<SearchProductDTO> SearchProductByName(String name) {
        return productInterface.findByNameContainingIgnoreCase(name).stream().map(product -> new SearchProductDTO(
                product.getName(),
                product.getPrices().stream().map(price -> new PriceSearchProductDTO(
                        price.getQuincaillerie().getStoreName(),
                        price.getPrice(),
                        price.getStock(),
                        price.getPromotionRating(),
                        price.getQuincaillerie().getLatitude(),
                        price.getQuincaillerie().getLongitude()
                )).toList(),
                product.getDescription()
        )).toList();
    }
}
