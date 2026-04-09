package com.ict300.P04.Service.panier;

import com.ict300.P04.DTO.product.response.ProductPanierDTO;
import com.ict300.P04.Entite.Panier;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.ProductExistException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.panier.PanierInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PanierService {

    @Autowired
    private PanierInterface panierInterface;

    @Autowired
    private CustomerInterface customerInterface;

    @Autowired
    private PriceInterface priceInterface;


    @Transactional
    public void addToPanier(String idPrice , String idUser){
        User user = customerInterface.getByIdUser(idUser);

        if(user == null){
            throw new UserNotFoundException("L'utilisateur n'existe pas");
        }

        Price price = priceInterface.getByIdPrice(idPrice);

        if(price == null) {
            throw  new ProductNotFoundException("Le produit n'existe pas");
        }

        int qte = panierInterface.ifAlreadyExistProdctInPanier(idPrice , idUser );

        if(qte != 0 ){
            throw new ProductExistException("Produit deja dans le panier");
        }

        Panier panier = new Panier();

        panier.setIdPanier(GenerateID.GeneratePanierID());
        panier.setUser(user);
        panier.setPrice(price);
        panier.setQuantity(1);
        panier.setDateAdded(LocalDateTime.now());

        panierInterface.save(panier);
    }

    @Transactional
    public  void deleteProductInPanier(String idPrice , String idUser) {

        int qte = panierInterface.ifAlreadyExistProdctInPanier(idPrice , idUser );

        if(qte == 0 ){
            throw new ProductNotFoundException("Produit n'existe pas dans le panier");
        }

        panierInterface.deleteProductInPanier(idPrice , idUser);
    }

    public int getQuantityProductInPanier(String idPrice , String idUser) {
        Price price = priceInterface.getByIdPrice(idPrice);

        if(price == null) {
            throw new ProductNotFoundException("Le price n'existe pas");
        }

        return panierInterface.ifAlreadyExistProdctInPanier(idPrice , idUser );

    }

    @Transactional
    public List<ProductPanierDTO> getAllProductInPanier(String idUser) {

        User user = customerInterface.getByIdUser(idUser);

        if (user == null) {
            throw new UserNotFoundException("L'utilisateur n'existe pas");
        }

        List<Object[]> results = panierInterface.findPanierByUser(user);

        return results.stream().map(result -> {
            Panier item = (Panier) result[0];

            // CORRECTION ICI : Utilisation de Number pour éviter le ClassCastException
            Double tauxRemise = (result[1] != null) ? ((Number) result[1]).doubleValue() : 0.0;

            BigDecimal originalPrice = item.getPrice().getPrice();
            boolean hasPromo = (tauxRemise > 0);

            Double priceWithPromo = 0.0;
            if (hasPromo) {
                // On calcule le prix remisé
                priceWithPromo = originalPrice.doubleValue() * (1 - (tauxRemise / 100));
            } else {
                // Si pas de promo, le prix promo est égal au prix original (ou 0 selon ton DTO)
                priceWithPromo = originalPrice.doubleValue();
            }

            return new ProductPanierDTO(
                    item.getPrice().getIdPrice(),
                    item.getPrice().getQuincaillerie().getIdQuincaillerie(),
                    item.getPrice().getProduct().getName(),
                    item.getPrice().getQuincaillerie().getStoreName(),
                    hasPromo,
                    priceWithPromo,
                    originalPrice,
                    item.getQuantity(),
                    item.getPrice().getProduct().getImageUrl()
            );
        }).toList();
    }

    @Transactional
    public  void deletePanier(String idQuincaillerie , String idUser) {
        panierInterface.deletePanierByQuincaillerie(idQuincaillerie , idUser);
    }

    @Transactional
    public void deleteAllPaniers(String idUser) {
        User user = customerInterface.getByIdUser(idUser);
        if(user == null){
            throw new UserNotFoundException("Utilisateur inexistant");
        }
        panierInterface.deletePanierByUser(user);
    }

    @Transactional
    public void addQuantityToPanier(String idPrice, String idUser) {

        Panier panier = panierInterface.findProductInPanierByUser(idUser , idPrice);

        if(panier == null){
            throw new ProductNotFoundException("Le price n'existe pas");
        }

        int quantite = panier.getQuantity() + 1;
        panier.setQuantity(quantite);

        panierInterface.save(panier);
    }

    @Transactional
    public void removeQuantityToPanier(String idPrice, String idUser) {

        Panier panier = panierInterface.findProductInPanierByUser(idUser , idPrice);

        if(panier == null){
            throw new ProductNotFoundException("Le price n'existe pas");
        }

        int quantite = panier.getQuantity() - 1;
        panier.setQuantity(quantite);

        panierInterface.save(panier);
    }
}
