package com.ict300.P04.Service.quincaillerie;

import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuincaillerieService {
    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    public QuincaillerieDetailsDTO getInfoQuincaillerei(String idQuincaillerie){

        QuincaillerieDetailsDTO quincaillerieDetailsDTO = new QuincaillerieDetailsDTO();
        Quincaillerie quincaillerie = quincaillerieInterface.getQuincaillerie(idQuincaillerie);

         quincaillerieDetailsDTO.setName(quincaillerie.getStoreName());
         quincaillerieDetailsDTO.setQuartier(quincaillerie.getQuartier());
         quincaillerieDetailsDTO.setAverageRating(quincaillerie.getAverageRating());
         quincaillerieDetailsDTO.setVille(quincaillerie.getCity());
         quincaillerieDetailsDTO.setStatus(quincaillerie.getStatus());
         quincaillerieDetailsDTO.setTelephone(quincaillerie.getPhone());

         return quincaillerieDetailsDTO;
    }
}
