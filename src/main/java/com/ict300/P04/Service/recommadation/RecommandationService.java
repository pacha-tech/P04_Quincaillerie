package com.ict300.P04.Service.recommadation;

import com.ict300.P04.DTO.recommadation.response.RecommendedProductDTO;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RecommandationService {

    @Autowired
    private ProductInterface productInterface;

    @Autowired
    private PriceInterface priceInterface;


    public List<RecommendedProductDTO> getRecommendations(String idProduct, String idQuincaillerie) {

        Product currentProduct = productInterface.findById(idProduct)
                .orElseThrow(() -> new ResourceNotFoundException("Produit source non trouvé"));


        List<Object[]> similarResults = productInterface.findRecommendationByProductAndCategoryAndQuincaillerie(currentProduct.getCategory().getIdCategory(), idProduct, idQuincaillerie);

        return similarResults.stream().map(result -> {
            Product p = (Product) result[0];

            // LA CORRECTION : On cast en Number pour extraire la valeur en double sans erreur
            Double tauxRemise = (result[1] != null) ? ((Number) result[1]).doubleValue() : 0.0;

            Price priceEntity = priceInterface.getPriceByProductAndQuincaillerie(p.getIdProduct(), idQuincaillerie);

            BigDecimal originalPrice = (priceEntity != null) ? priceEntity.getPrice() : BigDecimal.ZERO;
            String priceId = (priceEntity != null) ? priceEntity.getIdPrice() : "N/A";
            int currentStock = (priceEntity != null) ? priceEntity.getStock() : 0;

            boolean hasPromo = (tauxRemise > 0);
            double finalPrice = originalPrice.doubleValue(); // Prix de base par défaut
            Double tauxFinal = 0.0;

            if (hasPromo) {
                finalPrice = originalPrice.doubleValue() * (1 - (tauxRemise / 100));
                tauxFinal = tauxRemise;
            }

            int score = calculateScore(currentProduct, p);

            return new RecommendedProductDTO(
                    priceId,
                    p.getName(),
                    originalPrice,
                    p.getDescription(),
                    currentStock,
                    score,
                    p.getUnit(),
                    finalPrice,
                    hasPromo,
                    p.getImageUrl(),
                    tauxFinal.toString()
            );
        }).sorted((a, b) -> Integer.compare(b.getScore(), a.getScore())).toList();

    }


    private int calculateScore(Product current, Product target) {
        int score = 0;

        // Bonus Marque
        if (current.getBrand() != null && target.getBrand() != null
                && current.getBrand().equalsIgnoreCase(target.getBrand())) {
            score += 2;
        }

        // Calcul des prix minimums pour la comparaison de budget
        double currentMinPrice = getMinPriceValue(current);
        double targetMinPrice = getMinPriceValue(target);

        // Bonus Proximité de prix (+/- 10%)
        if (currentMinPrice > 0) {
            double rangeMin = currentMinPrice * 0.9;
            double rangeMax = currentMinPrice * 1.1;

            if (targetMinPrice >= rangeMin && targetMinPrice <= rangeMax) {
                score += 1;
            }
        }

        return score;
    }

    private double getMinPriceValue(Product product) {
        if (product.getPrices() == null) return 0.0;

        return product.getPrices().stream()
                .map(Price::getPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .min()
                .orElse(0.0);
    }
}