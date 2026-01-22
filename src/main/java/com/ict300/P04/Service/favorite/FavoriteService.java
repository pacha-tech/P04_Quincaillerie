package com.ict300.P04.Service.favorite;

import com.ict300.P04.DTO.favorite.request.AddFavoriteQuincaillerieDTO;
import com.ict300.P04.DTO.favorite.request.DeleteFavoriteQuincaillerieDTO;
import com.ict300.P04.Entite.FavoriteQuincaillerie;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.favoriteQuincaillerie.FavoriteQuincaillerieInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.UserInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteQuincaillerieInterface favoriteQuincaillerieInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Autowired
    private UserInterface userInterface;

    public void addFavoriteQuincaillerie(AddFavoriteQuincaillerieDTO addFavoriteQuincaillerieDTO){

        if(favoriteQuincaillerieInterface.existsByUserIdUserAndQuincaillerieIdQuincaillerie(addFavoriteQuincaillerieDTO.getIdUser(), addFavoriteQuincaillerieDTO.getIdQuincaillerie())){
            throw new RuntimeException("Produit deja en favoris");
        }

        User user = userInterface.getUser(addFavoriteQuincaillerieDTO.getIdUser());
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(addFavoriteQuincaillerieDTO.getIdQuincaillerie());

        FavoriteQuincaillerie favoriteQuincaillerie = new FavoriteQuincaillerie();


        favoriteQuincaillerie.setIdFavoriteQuincaillerie(GenerateID.GenerateFavoriteQuincaillerieID());
        favoriteQuincaillerie.setUser(user);
        favoriteQuincaillerie.setQuincaillerie(quincaillerie);
        favoriteQuincaillerie.setDateAdded(LocalDateTime.now());

        favoriteQuincaillerieInterface.save(favoriteQuincaillerie);

    }

    @Transactional
    public void deleteFavoriteQuincaillerie(DeleteFavoriteQuincaillerieDTO deleteFavoriteQuincaillerieDTO){
        favoriteQuincaillerieInterface.deleteByUserIdUserAndQuincaillerieIdQuincaillerie(deleteFavoriteQuincaillerieDTO.getIdUser(), deleteFavoriteQuincaillerieDTO.getIdQuincaillerie());
    }
}
