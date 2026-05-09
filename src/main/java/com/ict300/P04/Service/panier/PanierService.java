package com.ict300.P04.Service.panier;

import com.ict300.P04.DTO.product.response.ProductPanierDTO;
import com.ict300.P04.Entite.LignePanier;
import com.ict300.P04.Entite.Panier;
import com.ict300.P04.Entite.Price;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.ProductExistException;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Exception.ResourceNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.lignePanier.LignePanierInterface;
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

    @Autowired
    private LignePanierInterface lignePanierInterface;


    @Transactional
    public void addToPanier(String idPrice, String idUser) {

        User user = customerInterface.getByIdUser(idUser)
                .orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Price price = priceInterface.findByIdPrice(idPrice)
                .orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(idUser).orElse(null);

        if (panier == null) {
            panier = new Panier();
            panier.setIdPanier(GenerateID.GeneratePanierID());
            panier.setUser(user);
            panier.setCreationDate(LocalDateTime.now());
            panier.setUpdateDate(LocalDateTime.now());
            panier = panierInterface.save(panier);
        }

        if(price.getStock() > 0 ){
            LignePanier nouvelleLigne = new LignePanier();
            nouvelleLigne.setIdLignePanier(GenerateID.GenerateLignePanierID());
            nouvelleLigne.setPanier(panier);
            nouvelleLigne.setPrice(price);
            nouvelleLigne.setQuantity(1);

            panier.setUpdateDate(LocalDateTime.now());
            panierInterface.save(panier);
            lignePanierInterface.save(nouvelleLigne);
        }else {
            throw new ProductNotFoundException("Le stock de produit est finis");
        }
    }

    @Transactional
    public  void deleteProductInPanier(String idPrice , String idUser) {

        User user = customerInterface.getByIdUser(idUser)
                .orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Price price = priceInterface.findByIdPrice(idPrice)
                .orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(idUser).orElseThrow(() -> new ResourceNotFoundException("Cette Utilisateur n'a pas de panier"));

        lignePanierInterface.deleteProductInPanier(idPrice , panier);

        panier.setUpdateDate(LocalDateTime.now());
        panierInterface.save(panier);
    }

    public int getQuantityProductInPanier(String idPrice , String idUser) {
        User user = customerInterface.getByIdUser(idUser).orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Price price = priceInterface.findByIdPrice(idPrice).orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(idUser).orElseThrow(() -> new ResourceNotFoundException("Cette Utilisateur n'a pas de panier"));

        return lignePanierInterface.findQuantityByProductInPanier(idPrice , panier);

    }

    @Transactional(readOnly = true)
    public List<ProductPanierDTO> getAllProductInPanier(String idUser) {

        User user = customerInterface.getByIdUser(idUser).orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        List<Object[]> results = lignePanierInterface.findLignesPanierWithPromoByUser(user);

        return results.stream().map(result -> {

            LignePanier item = (LignePanier) result[0];

            double tauxRemise = ((Number) result[1]).doubleValue();

            BigDecimal originalPrice = item.getPrice().getPrice();
            boolean hasPromo = (tauxRemise > 0);

            Double priceWithPromo = hasPromo
                    ? originalPrice.doubleValue() * (1 - (tauxRemise / 100))
                    : originalPrice.doubleValue();

            return new ProductPanierDTO(
                    item.getPrice().getIdPrice(),
                    item.getPrice().getQuincaillerie().getIdQuincaillerie(),
                    item.getPrice().getProduct().getName(),
                    item.getPrice().getQuincaillerie().getStoreName(),
                    hasPromo,
                    priceWithPromo,
                    originalPrice,
                    item.getQuantity(),
                    item.getPrice().getProduct().getImageUrl(),
                    item.getPrice().getStock()
            );
        }).toList();
    }

    @Transactional
    public  void deletePanier(String idQuincaillerie , String idUser) {
        User user = customerInterface.getByIdUser(idUser).orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(idUser).orElseThrow(() -> new ResourceNotFoundException("Cette Utilisateur n'a pas de panier"));

        lignePanierInterface.deletePanierByQuincaillerie(idQuincaillerie , user);
    }

    @Transactional
    public void deleteAllPaniers(String idUser) {
        User user = customerInterface.getByIdUser(idUser).orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(idUser).orElseThrow(() -> new ResourceNotFoundException("Cette Utilisateur n'a pas de panier"));

        panierInterface.delete(panier);
    }

    @Transactional
    public void addQuantityToPanier(String idPrice, String idUser) {
        User user = customerInterface.getByIdUser(idUser).orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Price price = priceInterface.findByIdPrice(idPrice).orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(idUser).orElseThrow(() -> new ResourceNotFoundException("Cette Utilisateur n'a pas de panier"));

        LignePanier lignePanier = lignePanierInterface.getProductInPanier(idPrice,panier);
        int quantite = lignePanier.getQuantity() + 1;
        if(quantite <= lignePanier.getPrice().getStock()) {
            lignePanier.setQuantity(quantite);

            lignePanierInterface.save(lignePanier);
        }else {
            throw new ProductNotFoundException("le stock est epuisé");
        }
    }

    @Transactional
    public void removeQuantityToPanier(String idPrice, String idUser) {
        User user = customerInterface.getByIdUser(idUser).orElseThrow(() -> new UserNotFoundException("L'utilisateur n'existe pas"));

        Price price = priceInterface.findByIdPrice(idPrice).orElseThrow(() -> new ProductNotFoundException("Le produit n'existe pas"));

        Panier panier = panierInterface.findPanierByUser(idUser).orElseThrow(() -> new ResourceNotFoundException("Cette Utilisateur n'a pas de panier"));

        LignePanier lignePanier = lignePanierInterface.getProductInPanier(idPrice,panier);
        int nouvelleQuantite = lignePanier.getQuantity() - 1;

        if (nouvelleQuantite <= 0) {
            lignePanierInterface.delete(lignePanier);
        } else {
            lignePanier.setQuantity(nouvelleQuantite);
            lignePanierInterface.save(lignePanier);
        }
    }
}
