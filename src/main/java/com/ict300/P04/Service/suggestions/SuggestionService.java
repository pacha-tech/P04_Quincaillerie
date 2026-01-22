package com.ict300.P04.Service.suggestions;

import com.ict300.P04.DTO.suggestions.response.SuggestionSearchDTO;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import com.ict300.P04.repository.interfaces.product.ProductInterface;
import com.ict300.P04.repository.interfaces.quincaillerie.QuincaillerieInterface;
import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {
    @Autowired
    private CategoryInterface categoryInterface;

    @Autowired
    private ProductInterface productInterface;

    @Autowired
    private QuincaillerieInterface quincaillerieInterface;

    public List<SuggestionSearchDTO> getAllSuggestions(String name){
        String query = name.toLowerCase();
        List<SuggestionSearchDTO> suggestionSearchDTOs = new ArrayList<>();

        productInterface.findNameOnly(query)
                .forEach(item -> suggestionSearchDTOs.add(new SuggestionSearchDTO(item , "produit")));
        categoryInterface.findNameOnly(query)
                .forEach(item -> suggestionSearchDTOs.add(new SuggestionSearchDTO(item , "categorie")));
        quincaillerieInterface.findNameOnly(query)
                .forEach(item -> suggestionSearchDTOs.add(new SuggestionSearchDTO(item , "quincaillerie")));

        return suggestionSearchDTOs;
    }
}
