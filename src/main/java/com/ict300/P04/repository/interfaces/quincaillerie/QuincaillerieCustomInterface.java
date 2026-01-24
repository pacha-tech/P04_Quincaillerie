package com.ict300.P04.repository.interfaces.quincaillerie;

import com.ict300.P04.DTO.quincaillerie.response.QuincaillerieDetailsDTO;
import com.ict300.P04.Entite.Quincaillerie;

import java.util.List;

public interface QuincaillerieCustomInterface {
    List<String> findNameOnly(String name);
    Quincaillerie getQuincaillerie(String idQuincaillerie);
}
