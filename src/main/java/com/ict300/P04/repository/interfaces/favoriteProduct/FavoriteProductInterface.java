package com.ict300.P04.repository.interfaces.favoriteProduct;

import com.ict300.P04.Entite.FavoriteProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteProductInterface extends JpaRepository<FavoriteProduct , String> , FavoriteProductCustomInterface {
    boolean existsByUserIdUserAndProductIdProduct(String idUser, String idProduct);
    void deleteByUserIdUserAndProductIdProduct(String idUser, String idProduct);
}
