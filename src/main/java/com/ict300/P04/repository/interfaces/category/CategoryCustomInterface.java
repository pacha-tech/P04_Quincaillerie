package com.ict300.P04.repository.interfaces.category;

import com.ict300.P04.Entite.Category;

import java.util.List;

public interface CategoryCustomInterface {
    List<String> findNameOnly();
    Category getCategoty(String idCategory);
}
