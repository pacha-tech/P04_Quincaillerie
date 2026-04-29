package com.ict300.P04.Service.promotion;

import com.ict300.P04.DTO.price.response.PriceSearchProductDTO;
import com.ict300.P04.DTO.product.response.SearchProductDTO;
import com.ict300.P04.DTO.promotion.request.AddPromotionDTO;
import com.ict300.P04.DTO.promotion.response.PromotionDTO;
import com.ict300.P04.DTO.promotion.response.ProduitPromotionDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.AppException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.campagnePromotion.CampagnePromotionInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.promotion.PromotionInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionInterface promotionInterface;

    @Autowired
    private CampagnePromotionInterface campagnePromotionInterface;

    @Autowired
    private PriceInterface priceInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

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


            Price price = priceInterface.findByIdPrice(idPrice);
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

        // 3. Créer la campagne
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
            throw new AppException("Le Camapagne " + idCampagnePromotion + " n'existe pas");
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

    public List<SearchProductDTO> getAllProduitInPromotionGrouped() {

        List<Object[]> results = priceInterface.findPricesWithActivePromotion();


        Map<Product, List<PriceSearchProductDTO>> groupedByProduct = results.stream()
                .collect(Collectors.groupingBy(
                        result -> ((Price) result[0]).getProduct(),
                        Collectors.mapping(this::mapToPriceSearchDTO, Collectors.toList())
                ));


        return groupedByProduct.entrySet().stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    List<PriceSearchProductDTO> prices = entry.getValue();

                    SearchProductDTO dto = new SearchProductDTO();
                    dto.setIdProduct(product.getIdProduct());
                    dto.setIdCategory(product.getCategory().getIdCategory());
                    dto.setName(product.getName());
                    dto.setUnite(product.getUnit());
                    dto.setImageUrl(product.getImageUrl());
                    dto.setDescription(product.getDescription());
                    dto.setPriceSearchProductsDTO(prices);

                    return dto;
                })
                .collect(Collectors.toList());
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