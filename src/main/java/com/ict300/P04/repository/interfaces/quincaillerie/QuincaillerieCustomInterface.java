package com.ict300.P04.repository.interfaces.quincaillerie;

import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.Entite.Quincaillerie;

import java.util.List;
import java.util.Optional;

public interface QuincaillerieCustomInterface {
    List<String> findNameOnly();
    Optional<Quincaillerie> getQuincaillerie(String idQuincaillerie);
}
