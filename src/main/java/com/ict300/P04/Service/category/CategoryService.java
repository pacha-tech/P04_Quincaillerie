package com.ict300.P04.Service.category;

import com.ict300.P04.DTO.category.request.CategoryDTO;
import com.ict300.P04.DTO.category.response.AddCategoryDTO;
import com.ict300.P04.DTO.category.response.ProductInCategoryDTO;
import com.ict300.P04.DTO.localisation.LocalisationDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.Entite.Category;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Product;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Service.localisation.LocalisationService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.GeoUtils;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CategoryService {
    @Autowired
    private CategoryInterface categoryInterface;

    @Autowired
    private PriceInterface priceInterface;

    @Autowired
    private LocalisationService localisationService;

    public void AddCategory(AddCategoryDTO addCategoryDTO){
        Category newCategory = new Category();

        newCategory.setIdCategory(GenerateID.GenerateCategoryID());
        newCategory.setName(addCategoryDTO.getName());
        newCategory.setDescription(addCategoryDTO.getDescription());

        categoryInterface.save(newCategory);
    }

    public List<CategoryDTO> getAllCategory(){

        return categoryInterface.findAll().stream().map(category -> new CategoryDTO(
                category.getIdCategory(),
                category.getName(),
                category.getDescription()
        )).toList();
    }

    public List<ProductInCategoryDTO> getAllProductByCategory(String idCategory, Double longitude, Double latitude, String scope) {
        List<Object[]> results = priceInterface.findPricesByCategory(idCategory);
        List<ProductInCategoryDTO> filteredProducts = new ArrayList<>();


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


        for (Object[] res : results) {
            Price price = (Price) res[0];
            Product product = price.getProduct();
            Quincaillerie quincaillerie = price.getQuincaillerie();

            Double storeLat = quincaillerie.getLatitude() != null ? quincaillerie.getLatitude().doubleValue() : null;
            Double storeLng = quincaillerie.getLongitude() != null ? quincaillerie.getLongitude().doubleValue() : null;


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


            Double taux = (res[1] != null) ? ((Number) res[1]).doubleValue() : 0.0;
            boolean inPromo = taux > 0;

            BigDecimal pricePromo = null;
            if (inPromo) {
                double discountValue = price.getPrice().doubleValue() * (1 - (taux / 100));
                pricePromo = BigDecimal.valueOf(discountValue);
            }

            ProductInCategoryDTO dto = new ProductInCategoryDTO();
            dto.setIdPrice(price.getIdPrice());
            dto.setQuincaillerieName(price.getQuincaillerie().getStoreName());
            dto.setIdQuincaillerie(price.getQuincaillerie().getIdQuincaillerie());
            dto.setLatitudeQuincaillerie(price.getQuincaillerie().getLatitude());
            dto.setLongitudeQuincaillerie(price.getQuincaillerie().getLongitude());
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
