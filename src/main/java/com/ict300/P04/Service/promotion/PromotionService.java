package com.ict300.P04.Service.promotion;

import com.ict300.P04.DTO.promotion.request.AddPromotionDTO;
import com.ict300.P04.DTO.promotion.response.PromotionDTO;
import com.ict300.P04.DTO.promotion.response.ProduitPromotionDTO;
import com.ict300.P04.Entite.CampagnePromotion;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.Promotion;
import com.ict300.P04.Entite.Quincaillerie;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie);

        if (dto.getIdsPrices() == null || dto.getIdsPrices().isEmpty()) {
            throw new AppException("La liste des produits est vide");
        }

        if(quincaillerie == null ){
            throw new ResourceNotFoundException("La Quincaillerie n'existe pas");
        }

        List<Price> prices = new ArrayList<>();
        for (String idPrice : dto.getIdsPrices()) {


            Price price = priceInterface.getByIdPrice(idPrice);
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
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie);
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


    public List<PromotionDTO> getAllPromotionByQuincaillerie(String idQuincaillerie) {
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie);
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