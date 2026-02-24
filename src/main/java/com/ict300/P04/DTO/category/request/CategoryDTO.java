package com.ict300.P04.DTO.category.request;

import lombok.Data;

@Data
public class CategoryDTO {
    private String id;
    private String name;
    private String description;

    public CategoryDTO(String id , String name , String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public CategoryDTO(){}
}
