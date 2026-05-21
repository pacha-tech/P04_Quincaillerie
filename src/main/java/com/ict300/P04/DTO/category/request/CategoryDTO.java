package com.ict300.P04.DTO.category.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @AllArgsConstructor @NoArgsConstructor
public class CategoryDTO {
    private String idCategory;
    private String name;
    private String description;
}
