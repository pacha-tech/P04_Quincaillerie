package com.ict300.P04.repository.interfaces.favoriteQuincaillerie;

import com.ict300.P04.Entite.FavoriteQuincaillerie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteQuincaillerieInterface extends JpaRepository<FavoriteQuincaillerie , String> , FavoriteQuincaillerieCustomInterface {

}
