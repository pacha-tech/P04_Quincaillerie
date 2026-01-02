package com.ict300.P04.repository.interfacesImpl.favoriteQuincaillerie;

import com.ict300.P04.Entite.FavoriteQuincaillerie;
import com.ict300.P04.repository.interfaces.favoriteQuincaillerie.FavoriteQuincaillerieCustomInterface;

import java.util.List;

public class FavoriteQuincaillerieInterfaceImpl implements FavoriteQuincaillerieCustomInterface {
    @Override
    public List<FavoriteQuincaillerie> getFavoriteQuincaillerieByUser(String idUser) {
        return List.of();
    }
}
