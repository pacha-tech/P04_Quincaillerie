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

        boolean ifProductExistInPanier = panierInterface.ifAlreadyExistProdctInPanier(idPrice , idUser );

        if(ifProductExistInPanier){
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

        boolean ifProductExistInPanier = panierInterface.ifAlreadyExistProdctInPanier(idPrice , idUser );

        if(!ifProductExistInPanier){
            throw new ProductNotFoundException("Produit n'existe pas dans le panier");
        }

        panierInterface.deleteProductInPanier(idPrice , idUser);
    }

    public boolean checkIfProductExistInPanier(String idPrice , String idUser) {
        Price price = priceInterface.getByIdPrice(idPrice);

        if(price == null) {
            throw new ProductNotFoundException("Le price n'existe pas");
        }

        return panierInterface.ifAlreadyExistProdctInPanier(idPrice , idUser );

    }

    @Transactional
    public List<ProductPanierDTO> getAllProductInPanier(String idUser){

        User user = customerInterface.getByIdUser(idUser);

        if(user == null) {
            throw new UserNotFoundException("L'utilisateur n'existe pas");
        }

        return panierInterface.findPanierByUser(user).stream().map((item) -> new ProductPanierDTO(
                item.getPrice().getIdPrice(),
                item.getPrice().getQuincaillerie().getIdQuincaillerie(),
                item.getPrice().getProduct().getName(),
                item.getPrice().getQuincaillerie().getStoreName(),
                item.getPrice().getPrice(),
                item.getQuantity()
        )).toList();
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
}
