package com.ict300.P04.Service.category;

import com.ict300.P04.DTO.category.request.CategoryDTO;
import com.ict300.P04.DTO.category.response.AddCategoryDTO;
import com.ict300.P04.Entite.Category;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.category.CategoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryInterface categoryInterface;

    public void AddCategory(AddCategoryDTO addCategoryDTO){
        Category newCategory = new Category();

        newCategory.setIdCategory(GenerateID.GenerateCategoryID());
        newCategory.setName(addCategoryDTO.getName());
        newCategory.setDescription(addCategoryDTO.getDescription());

        categoryInterface.save(newCategory);
    }

    public List<CategoryDTO> getAllCategory(){

        return categoryInterface.findAll().stream().map(category -> new CategoryDTO(
                category.getIdCategory(),
                category.getName(),
                category.getDescription()
        )).toList();
    }
}
