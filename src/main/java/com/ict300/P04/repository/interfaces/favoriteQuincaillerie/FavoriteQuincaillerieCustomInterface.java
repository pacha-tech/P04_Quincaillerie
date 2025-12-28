package com.ict300.P04.repository.interfaces.favoriteQuincaillerie;

import com.ict300.P04.Entite.FavoriteQuincaillerie;

import java.util.List;

public interface FavoriteQuincaillerieCustomInterface {
    List<FavoriteQuincaillerie> getFavoriteQuincaillerieByUser(String idUser);
}
