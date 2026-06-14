package com.ict300.P04.Service.product;

import com.ict300.P04.DTO.category.response.ProductInCategoryDTO;
import com.ict300.P04.DTO.localisation.LocalisationDTO;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Service.localisation.LocalisationService;
import com.ict300.P04.Utilitaires.GeoUtils;
import com.ict300.P04.repository.interfaces.historiqueNavigation.HistoriqueNavigationInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PourVousService {

    @Autowired
    private HistoriqueNavigationInterface historiqueNavigationInterface;

    @Autowired
    private PriceInterface priceInterface;

    @Autowired
    LocalisationService localisationService;

    public List<ProductInCategoryDTO> getForYouProduct(String idUser, Double longitude, Double latitude, String scope) {
        List<ProductInCategoryDTO> filteredProducts = new ArrayList<>();
        String targetCategoryId = null;

        // 1. DÉTERMINER LA CATÉGORIE CIBLE (Historique utilisateur ou Tendance globale)
        List<String> userTopCategories = historiqueNavigationInterface.findTopCategoriesByUserId(idUser, PageRequest.of(0, 1));

        if (!userTopCategories.isEmpty()) {
            targetCategoryId = userTopCategories.get(0);
        } else {
            // Mode secours : On prend la catégorie la plus populaire de la plateforme
            List<String> globalTopCategories = historiqueNavigationInterface.findGlobalTopCategories(PageRequest.of(0, 1));
            if (!globalTopCategories.isEmpty()) {
                targetCategoryId = globalTopCategories.get(0);
            }
        }

        // Si la base est totalement vide (aucun historique global), on renvoie une liste vide
        if (targetCategoryId == null) {
            log.warn("Aucune donnée d'historique disponible. Retour d'une liste 'Pour vous' vide.");
            return filteredProducts;
        }

        // 2. RÉCUPÉRATION DES PRODUITS BRUTS DE LA CATÉGORIE CIBLE
        List<Object[]> results = priceInterface.findPricesByCategory(targetCategoryId);

        // 3. PRÉPARATION DES FILTRES GÉOGRAPHIQUES
        double maxDistanceKm = GeoUtils.convertScopeToKilometers(scope);
        boolean isCityScope = "ville".equalsIgnoreCase(scope);
        boolean isRegionScope = "region".equalsIgnoreCase(scope);

        String userCity = null;
        String userRegion = null;

        if (latitude != null && longitude != null && (isCityScope || isRegionScope)) {
            LocalisationDTO loc = localisationService.getAddress(latitude, longitude);
            userCity = loc.getVille();
            userRegion = loc.getRegion();
            log.info("Client localisé dans la ville: {}, région: {}", userCity, userRegion);
        }

        // 4. BOUCLE DE FILTRAGE ET MAPPING
        for (Object[] res : results) {
            Price price = (Price) res[0];
            Product product = price.getProduct();
            Quincaillerie quincaillerie = price.getQuincaillerie();

            Double storeLat = quincaillerie.getLatitude() != null ? quincaillerie.getLatitude().doubleValue() : null;
            Double storeLng = quincaillerie.getLongitude() != null ? quincaillerie.getLongitude().doubleValue() : null;

            // --- FILTRAGE GÉOGRAPHIQUE ---
            if (latitude != null && longitude != null) {
                if (isCityScope) {
                    if (userCity == null || "Non défini".equals(userCity) || !userCity.equalsIgnoreCase(quincaillerie.getCity())) {
                        continue;
                    }
                } else if (isRegionScope) {
                    if (userRegion == null || "Non défini".equals(userRegion) || !userRegion.equalsIgnoreCase(quincaillerie.getRegion())) {
                        continue;
                    }
                } else if (storeLat != null && storeLng != null) {
                    double distance = GeoUtils.calculateDistance(latitude, longitude, storeLat, storeLng);
                    if (distance > maxDistanceKm) {
                        continue;
                    }
                }
            }

            // --- CALCUL DES PROMOTIONS ---
            Double taux = (res[1] != null) ? ((Number) res[1]).doubleValue() : 0.0;
            boolean inPromo = taux > 0;

            BigDecimal pricePromo = null;
            if (inPromo) {
                double discountValue = price.getPrice().doubleValue() * (1 - (taux / 100));
                pricePromo = BigDecimal.valueOf(discountValue);
            }

            // --- MAPPING VERS ProductInCategoryDTO ---
            ProductInCategoryDTO dto = new ProductInCategoryDTO();
            dto.setIdPrice(price.getIdPrice());
            dto.setQuincaillerieName(quincaillerie.getStoreName());
            dto.setIdQuincaillerie(quincaillerie.getIdQuincaillerie());
            dto.setLatitudeQuincaillerie(quincaillerie.getLatitude());
            dto.setLongitudeQuincaillerie(quincaillerie.getLongitude());

            dto.setName(product.getName());
            dto.setBrand(product.getBrand());
            dto.setIdCategory(product.getCategory().getIdCategory());
            dto.setStock(price.getStock());
            dto.setUnit(product.getUnit());
            dto.setSellPrice(price.getPrice());
            dto.setImageUrl(product.getImageUrl());
            dto.setDescriptionProduit(product.getDescription());
            dto.setPurchasePrice(price.getPurchasePrice());

            dto.setPricepromo(pricePromo);
            dto.setInPromotion(inPromo);
            dto.setTaux(taux);

            filteredProducts.add(dto);
        }

        return filteredProducts;
    }
}