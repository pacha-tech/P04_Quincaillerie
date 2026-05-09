package com.ict300.P04.Service.quincaillerie;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.ict300.P04.DTO.quincaillerie.request.RegisterQuincaillerieDTO;
import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.ProductNotFoundException;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class QuincaillerieService {
    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    @Autowired
    private CustomerInterface userInterface;

    public QuincaillerieDetailsDTO getInfoQuincaillerei(String idQuincaillerie){

        QuincaillerieDetailsDTO quincaillerieDetailsDTO = new QuincaillerieDetailsDTO();
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie).orElse(null);

        if(quincaillerie == null){
            throw new ProductNotFoundException("La quincaillerie n'existe pas");
        }

        quincaillerieDetailsDTO.setName(quincaillerie.getStoreName());
        quincaillerieDetailsDTO.setRegion(quincaillerie.getRegion());
        quincaillerieDetailsDTO.setVille(quincaillerie.getCity());
        quincaillerieDetailsDTO.setQuartier(quincaillerie.getQuartier());
        quincaillerieDetailsDTO.setPrecision(quincaillerie.getPrecision());
        quincaillerieDetailsDTO.setTelephone(quincaillerie.getPhone());
        quincaillerieDetailsDTO.setAverageRating(quincaillerie.getAverageRating());
        quincaillerieDetailsDTO.setPhotoUrl(quincaillerie.getPhotoUrl());
        quincaillerieDetailsDTO.setLongitude(quincaillerie.getLongitude());
        quincaillerieDetailsDTO.setLatitude(quincaillerie.getLatitude());
        quincaillerieDetailsDTO.setStatus(quincaillerie.getStatus());
        quincaillerieDetailsDTO.setStatus(quincaillerie.getStatus());

         return quincaillerieDetailsDTO;
    }

    public Quincaillerie registerQuincaillerie(RegisterQuincaillerieDTO registerQuincaillerieDTO , String idUser){

        Quincaillerie newQuincaillerie = new Quincaillerie();
        User admin = userInterface.getByIdUser(idUser).orElse(null);


        newQuincaillerie.setIdQuincaillerie(GenerateID.GenerateQuincaillerieID());
        newQuincaillerie.setAdmin(admin);
        newQuincaillerie.setStoreName(registerQuincaillerieDTO.getStoreName());
        newQuincaillerie.setCity(registerQuincaillerieDTO.getVille());
        newQuincaillerie.setQuartier(registerQuincaillerieDTO.getQuartier());
        newQuincaillerie.setDescription(registerQuincaillerieDTO.getDescription());
        newQuincaillerie.setLatitude(registerQuincaillerieDTO.getLatitude());
        newQuincaillerie.setLongitude(registerQuincaillerieDTO.getLongitude());
        newQuincaillerie.setAverageRating(null);
        newQuincaillerie.setPrecision(registerQuincaillerieDTO.getPrecision());
        newQuincaillerie.setRegion(registerQuincaillerieDTO.getRegion());
        newQuincaillerie.setPhone(registerQuincaillerieDTO.getPhone());
        newQuincaillerie.setNoticeCount(0);
        newQuincaillerie.setCreationDate(LocalDateTime.now());
        newQuincaillerie.setPhotoUrl(registerQuincaillerieDTO.getPhotoUrl());
        newQuincaillerie.setStatus("ACTIF");
        newQuincaillerie.setNui(registerQuincaillerieDTO.getNui());
        newQuincaillerie.setAcceptTerms(registerQuincaillerieDTO.getAcceptTerms());
        newQuincaillerie.setWantTips(registerQuincaillerieDTO.getWantTips());



        quincaillerieInterface.save(newQuincaillerie);

        return newQuincaillerie;
    }
}
