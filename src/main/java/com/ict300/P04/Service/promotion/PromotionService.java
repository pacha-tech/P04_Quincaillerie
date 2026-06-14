package com.ict300.P04.Service.promotion;

import com.ict300.P04.DTO.localisation.LocalisationDTO;
import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.promotion.request.AddPromotionDTO;
import com.ict300.P04.DTO.promotion.response.PromotionDTO;
import com.ict300.P04.DTO.promotion.response.ProduitPromotionDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.AppException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Service.localisation.LocalisationService;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.Utilitaires.GeoUtils;
import com.ict300.P04.repository.interfaces.campagnePromotion.CampagnePromotionInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.promotion.PromotionInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class PromotionService {

    @Autowired
    private PromotionInterface promotionInterface;

    @Autowired
    private CampagnePromotionInterface campagnePromotionInterface;

    @Autowired
    private PriceInterface priceInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Autowired
    private LocalisationService localisationService;

    @Transactional
    public void addPromotion(AddPromotionDTO dto, String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie).orElse(null);

        if (dto.getIdsPrices() == null || dto.getIdsPrices().isEmpty()) {
            throw new AppException("La liste des produits est vide");
        }

        if(quincaillerie == null ){
            throw new ResourceNotFoundException("La Quincaillerie n'existe pas");
        }

        List<Price> prices = new ArrayList<>();
        for (String idPrice : dto.getIdsPrices()) {
            Price price = priceInterface.findByIdPrice(idPrice).orElse(null);
            if (price == null) {
                throw new AppException("Le produit " + idPrice + " n'existe pas");
            }

            if (!price.getQuincaillerie().equals(quincaillerie)) {
                throw new AppException("Le produit " + idPrice + " n'appartient pas à votre quincaillerie");
            }

            boolean chevauchement = promotionInterface.existsActivePromoForPeriod(idPrice, dto.getDateDebut(), dto.getDateFin());
            if (chevauchement) {
                throw new AppException("Le produit " + idPrice + " a déjà une promotion active sur cette période");
            }
            prices.add(price);
        }

        CampagnePromotion campagne = new CampagnePromotion();
        campagne.setIdCampagnePromotion(GenerateID.GenerateCampagneID());
        campagne.setNom(dto.getNom());
        campagne.setTauxRemise(dto.getTauxRemise());
        campagne.setDateDebut(dto.getDateDebut());
        campagne.setDateFin(dto.getDateFin());
        campagne.setEstActif(true);
        campagne.setQuincaillerie(quincaillerie);

        campagnePromotionInterface.save(campagne);

        for (Price price : prices) {
            Promotion promotion = new Promotion();
            promotion.setIdPromotion(GenerateID.GeneratePromotionID());
            promotion.setPrice(price);
            promotion.setCampagnePromotion(campagne);
            promotionInterface.save(promotion);
        }
    }

    @Transactional
    public void deletePromotion(String idCampagnePromotion , String idQuincaillerie) {
        CampagnePromotion campagnePromotion = campagnePromotionInterface.getByIdCampagne(idCampagnePromotion);

        if(campagnePromotion == null ){
            throw new AppException("La Campagne " + idCampagnePromotion + " n'existe pas");
        }

        if(!Objects.equals(campagnePromotion.getQuincaillerie().getIdQuincaillerie(), idQuincaillerie)) {
            throw new AppException("La campagne " + idCampagnePromotion + " n'appartient pas à votre quincaillerie");
        }

        campagnePromotion.setEstActif(false);
        campagnePromotionInterface.save(campagnePromotion);
    }

    public List<ProduitPromotionDTO> getAllProduitOutPromotionByQuincaillerie(String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie).orElse(null);
        if(quincaillerie == null ) {
            throw new ResourceNotFoundException("La Quincaillerie n'existe pas");
        }

        List<Price> prices = priceInterface.findPricesWithoutActivePromotion(idQuincaillerie);

        return prices.stream().map(price -> {
            assert price.getProduct() != null;
            return new ProduitPromotionDTO(
                    price.getIdPrice(),
                    price.getProduct().getName(),
                    price.getProduct().getCategory() != null ? price.getProduct().getCategory().getName() : "",
                    price.getProduct().getImageUrl(),
                    price.getPrice()
            );
        }).toList();
    }

    /*
    public List<SearchProductDTO> getAllProduitInPromotionGrouped(Double latitude , Double longitude , String scope) {
        List<Object[]> results = priceInterface.findPricesWithActivePromotion();

        Map<Product, List<Object[]>> groupedByProduct = results.stream()
                .collect(Collectors.groupingBy(
                        result -> ((Price) result[0]).getProduct()
                ));

        Stream<Map.Entry<Product, List<Object[]>>> stream = groupedByProduct.entrySet().stream();

        // ◄ UTILISATION DU NOUVEL UTILITAIRE POUR LE TRI PAR DISTANCE
        if (latitude != null && longitude != null) {
            stream = stream.peek(entry -> entry.getValue().sort(Comparator.comparingDouble(res ->
                            GeoUtils.calculateDistance(latitude, longitude,
                                    ((Price) res[0]).getQuincaillerie().getLatitude().doubleValue(),
                                    ((Price) res[0]).getQuincaillerie().getLongitude().doubleValue())
                    )))
                    .sorted(Comparator.comparingDouble(entry ->
                            GeoUtils.calculateDistance(latitude, longitude,
                                    ((Price) entry.getValue().get(0)[0]).getQuincaillerie().getLatitude().doubleValue(),
                                    ((Price) entry.getValue().get(0)[0]).getQuincaillerie().getLongitude().doubleValue())
                    ));
        }

        return stream.map(entry -> {
            Product product = entry.getKey();

            List<PriceSearchProductDTO> prices = entry.getValue().stream()
                    .map(this::mapToPriceSearchDTO)
                    .collect(Collectors.toList());

            SearchProductDTO dto = new SearchProductDTO();
            dto.setIdProduct(product.getIdProduct());
            dto.setIdCategory(product.getCategory().getIdCategory());
            dto.setName(product.getName());
            dto.setUnite(product.getUnit());
            dto.setImageUrl(product.getImageUrl());
            dto.setDescription(product.getDescription());
            dto.setPriceSearchProductsDTO(prices);

            return dto;
        }).collect(Collectors.toList());
    }
     */

    public List<SearchProductDTO> getAllProduitInPromotionGrouped(Double latitude, Double longitude, String scope) {
        // Récupération brute
        List<Object[]> results = priceInterface.findPricesWithActivePromotion();

        // 1. FILTRAGE PAR SCOPE (Ville, Région, ou Rayon Km)
        if (latitude != null && longitude != null) {
            double maxDistanceKm = GeoUtils.convertScopeToKilometers(scope);
            boolean isCityScope = "ville".equalsIgnoreCase(scope);
            boolean isRegionScope = "region".equalsIgnoreCase(scope);

            String userCity = null;
            String userRegion = null;

            // Récupération de l'adresse utilisateur si besoin
            if (isCityScope || isRegionScope) {
                LocalisationDTO loc = localisationService.getAddress(latitude, longitude);
                userCity = loc.getVille();
                userRegion = loc.getRegion();
            }

            final String finalUserCity = userCity;
            final String finalUserRegion = userRegion;

            // Filtrage du flux de résultats
            results = results.stream().filter(res -> {
                Price price = (Price) res[0];
                Quincaillerie quincaillerie = price.getQuincaillerie();

                Double storeLat = quincaillerie.getLatitude() != null ? quincaillerie.getLatitude().doubleValue() : null;
                Double storeLng = quincaillerie.getLongitude() != null ? quincaillerie.getLongitude().doubleValue() : null;

                if (isCityScope) {
                    return finalUserCity != null && !"Non défini".equals(finalUserCity) && finalUserCity.equalsIgnoreCase(quincaillerie.getCity());
                } else if (isRegionScope) {
                    return finalUserRegion != null && !"Non défini".equals(finalUserRegion) && finalUserRegion.equalsIgnoreCase(quincaillerie.getRegion());
                } else if (storeLat != null && storeLng != null) {
                    double distance = GeoUtils.calculateDistance(latitude, longitude, storeLat, storeLng);
                    return distance <= maxDistanceKm;
                }
                return false;
            }).toList();
        }

        // 2. GROUPEMENT PAR PRODUIT DES RÉSULTATS FILTRÉS
        Map<Product, List<Object[]>> groupedByProduct = results.stream()
                .collect(Collectors.groupingBy(
                        result -> ((Price) result[0]).getProduct()
                ));

        Stream<Map.Entry<Product, List<Object[]>>> stream = groupedByProduct.entrySet().stream();

        // 3. TRI PAR DISTANCE POUR CHAQUE PRODUIT ET ENTRE PRODUITS
        if (latitude != null && longitude != null) {
            stream = stream.peek(entry -> entry.getValue().sort(Comparator.comparingDouble(res ->
                            GeoUtils.calculateDistance(latitude, longitude,
                                    ((Price) res[0]).getQuincaillerie().getLatitude().doubleValue(),
                                    ((Price) res[0]).getQuincaillerie().getLongitude().doubleValue())
                    )))
                    .sorted(Comparator.comparingDouble(entry ->
                            GeoUtils.calculateDistance(latitude, longitude,
                                    ((Price) entry.getValue().get(0)[0]).getQuincaillerie().getLatitude().doubleValue(),
                                    ((Price) entry.getValue().get(0)[0]).getQuincaillerie().getLongitude().doubleValue())
                    ));
        }

        // 4. MAPPING VERS SearchProductDTO
        return stream.map(entry -> {
            Product product = entry.getKey();

            List<PriceSearchProductDTO> prices = entry.getValue().stream()
                    .map(this::mapToPriceSearchDTO)
                    .collect(Collectors.toList());

            SearchProductDTO dto = new SearchProductDTO();
            dto.setIdProduct(product.getIdProduct());
            dto.setIdCategory(product.getCategory().getIdCategory());
            dto.setName(product.getName());
            dto.setUnite(product.getUnit());
            dto.setImageUrl(product.getImageUrl());
            dto.setDescription(product.getDescription());
            dto.setPriceSearchProductsDTO(prices);

            return dto;
        }).collect(Collectors.toList());
    }

    private PriceSearchProductDTO mapToPriceSearchDTO(Object[] result) {
        Price price = (Price) result[0];
        Number tauxValue = (Number) result[1];
        Double tauxRemise = tauxValue.doubleValue();
        Quincaillerie store = price.getQuincaillerie();

        BigDecimal originalPrice = price.getPrice();
        BigDecimal taux = BigDecimal.valueOf(tauxRemise);
        BigDecimal cent = BigDecimal.valueOf(100);

        BigDecimal finalPrice = originalPrice.multiply(
                BigDecimal.ONE.subtract(taux.divide(cent, 2, RoundingMode.HALF_UP))
        );

        PriceSearchProductDTO priceDTO = new PriceSearchProductDTO();
        priceDTO.setIdPrice(price.getIdPrice());
        priceDTO.setIdQuincaillerie(store.getIdQuincaillerie());
        priceDTO.setQuincaillerieName(store.getStoreName());
        priceDTO.setPrice(originalPrice);
        priceDTO.setStock(price.getStock());

        priceDTO.setLatitudeQuincaillerie(store.getLatitude());
        priceDTO.setLongitudeQuincaillerie(store.getLongitude());

        priceDTO.setPricePromo(finalPrice.doubleValue());
        priceDTO.setInPromotion(true);
        priceDTO.setTaux(tauxRemise.toString());

        return priceDTO;
    }

    public List<PromotionDTO> getAllPromotionByQuincaillerie(String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie).orElse(null);
        if (quincaillerie == null) {
            throw new ResourceNotFoundException("La Quincaillerie n'existe pas");
        }

        List<Object[]> objects = campagnePromotionInterface.getByQuincaillerie(quincaillerie);

        return objects.stream().map(result -> {
            CampagnePromotion cp = (CampagnePromotion) result[0];
            Long nombreProduits = (Long) result[1];

            return new PromotionDTO(
                    cp.getIdCampagnePromotion(),
                    cp.getNom(),
                    cp.getTauxRemise().toString(),
                    cp.getDateDebut().toString(),
                    cp.getDateFin().toString(),
                    cp.getEstActif(),
                    nombreProduits.intValue()
            );
        }).toList();
    }
}