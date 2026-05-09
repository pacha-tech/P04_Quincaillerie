package com.ict300.P04.Service.favorite;

import com.ict300.P04.DTO.favorite.request.AddFavoriteProductDTO;
import com.ict300.P04.DTO.favorite.request.AddFavoriteQuincaillerieDTO;
import com.ict300.P04.DTO.favorite.request.DeleteFavoriteProductDTO;
import com.ict300.P04.DTO.favorite.request.DeleteFavoriteQuincaillerieDTO;
import com.ict300.P04.Entite.*;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.favoriteProduct.FavoriteProductInterface;
import com.ict300.P04.repository.interfaces.favoriteQuincaillerie.FavoriteQuincaillerieInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteQuincaillerieInterface favoriteQuincaillerieInterface;

    @Autowired
    private FavoriteProductInterface favoriteProductInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Autowired
    private ProductInterface productInterface;

    @Autowired
    private CustomerInterface userInterface;

    public void addFavoriteQuincaillerie(AddFavoriteQuincaillerieDTO addFavoriteQuincaillerieDTO){

        if(favoriteQuincaillerieInterface.existsByUserIdUserAndQuincaillerieIdQuincaillerie(addFavoriteQuincaillerieDTO.getIdUser(), addFavoriteQuincaillerieDTO.getIdQuincaillerie())){
            throw new RuntimeException("Quincaillerie deja en favoris");
        }

        User user = userInterface.getByIdUser(addFavoriteQuincaillerieDTO.getIdUser()).orElse(null);
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(addFavoriteQuincaillerieDTO.getIdQuincaillerie()).orElse(null);

        if(quincaillerie == null){
            throw new ProductNotFoundException("La quincaillerie n'existe pas");
        }

        if(user == null){
            throw new UserNotFoundException("L'utilisateur n'existe pas");
        }

        FavoriteQuincaillerie favoriteQuincaillerie = new FavoriteQuincaillerie();


        favoriteQuincaillerie.setIdFavoriteQuincaillerie(GenerateID.GenerateFavoriteQuincaillerieID());
        favoriteQuincaillerie.setUser(user);
        favoriteQuincaillerie.setQuincaillerie(quincaillerie);
        favoriteQuincaillerie.setDateAdded(LocalDateTime.now());

        favoriteQuincaillerieInterface.save(favoriteQuincaillerie);

    }

    @Transactional
    public void deleteFavoriteQuincaillerie(DeleteFavoriteQuincaillerieDTO deleteFavoriteQuincaillerieDTO){
        if(!favoriteQuincaillerieInterface.existsByUserIdUserAndQuincaillerieIdQuincaillerie(deleteFavoriteQuincaillerieDTO.getIdUser() , deleteFavoriteQuincaillerieDTO.getIdQuincaillerie())){
            throw new RuntimeException("Quincaillerie deja supprimé des favoris");
        }
        favoriteQuincaillerieInterface.deleteByUserIdUserAndQuincaillerieIdQuincaillerie(deleteFavoriteQuincaillerieDTO.getIdUser(), deleteFavoriteQuincaillerieDTO.getIdQuincaillerie());
    }

    public void addFavoriteProduct(AddFavoriteProductDTO addFavoriteProductDTO){

        if(favoriteProductInterface.existsByUserIdUserAndProductIdProduct(addFavoriteProductDTO.getIdUser(), addFavoriteProductDTO.getIdProduct())){
            throw new RuntimeException("Produit deja en favoris");
        }

        User user = userInterface.getByIdUser(addFavoriteProductDTO.getIdUser()).orElse(null);
        Product product = productInterface.getProduct(addFavoriteProductDTO.getIdProduct());

        FavoriteProduct favoriteProduct = new FavoriteProduct();

        favoriteProduct.setIdFavoriteProduct(GenerateID.GenerateFavoriteProductID());
        favoriteProduct.setProduct(product);
        favoriteProduct.setUser(user);
        favoriteProduct.setDateAdded(LocalDateTime.now());

        favoriteProductInterface.save(favoriteProduct);
    }

    @Transactional
    public void deleteFavoriteProduct(DeleteFavoriteProductDTO deleteFavoriteProductDTO){
        if(!favoriteProductInterface.existsByUserIdUserAndProductIdProduct(deleteFavoriteProductDTO.getIdUser(), deleteFavoriteProductDTO.getIdProduct())){
            throw new RuntimeException("Produit deja supprimé des favoris");
        }
        favoriteProductInterface.deleteByUserIdUserAndProductIdProduct(deleteFavoriteProductDTO.getIdUser(), deleteFavoriteProductDTO.getIdProduct());
    }
}
