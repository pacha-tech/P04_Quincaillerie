package com.ict300.P04.Service.recommadation;

import com.ict300.P04.DTO.recommadation.response.RecommendedProductDTO;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecommandationService {
    @Autowired
    private ProductInterface productInterface;

    public List<RecommendedProductDTO> getRecommendations(String IdProduct , String IdQuincaillerie ) {
        Product currentProduct = productInterface.findById(IdProduct)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));


        List<Product> similarProducts = productInterface.findRecommendationByProductAndCategoryAndQuincaillerie(
                currentProduct.getCategory().getIdCategory(),
                IdProduct,
                IdQuincaillerie
        );


        return similarProducts.stream()
                .map(p -> new RecommendedProductDTO(p.getName(), productInterface.findPriceByQuincaillerie(IdProduct , IdQuincaillerie) , p.getDescription() ,  calculateScore(currentProduct, p)))
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .limit(5)
                .map(recommendedProductDTO -> new RecommendedProductDTO(
                        recommendedProductDTO.getName(),
                        recommendedProductDTO.getPrice(),
                        recommendedProductDTO.getDescription(),
                        recommendedProductDTO.getScore()
                )).toList();
    }

    private int calculateScore(Product current, Product target) {
        int score = 0;

        if (current.getBrand() != null && current.getBrand().equals(target.getBrand())) {
            score += 2;
        }

        double currentMinPrice = current.getPrices().stream()
                .map(Price::getPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .min()
                .orElse(0.0);

        double targetMinPrice = target.getPrices().stream()
                .map(Price::getPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .min()
                .orElse(0.0);

        if (currentMinPrice > 0) {
            double rangeMin = currentMinPrice * 0.9;
            double rangeMax = currentMinPrice * 1.1;

            if (targetMinPrice >= rangeMin && targetMinPrice <= rangeMax) {
                score += 1;
            }
        }

        return score;
    }
}
