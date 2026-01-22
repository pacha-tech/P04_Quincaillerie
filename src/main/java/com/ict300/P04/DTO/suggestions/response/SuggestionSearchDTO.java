package com.ict300.P04.DTO.suggestions.response;

import lombok.Data;

@Data
public class SuggestionSearchDTO {
    private String label;
    private String type;

    public SuggestionSearchDTO(String label , String type){
        this.label = label;
        this.type = type;
    }

    public SuggestionSearchDTO(){}
}
