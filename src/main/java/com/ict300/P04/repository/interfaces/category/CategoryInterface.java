package com.ict300.P04.repository.interfaces.category;

import com.ict300.P04.Entite.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryInterface extends JpaRepository<Category , String> , CategoryCustomInterface {
}
