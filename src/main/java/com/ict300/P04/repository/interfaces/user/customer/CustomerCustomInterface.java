package com.ict300.P04.repository.interfaces.user.customer;

import com.ict300.P04.Entite.User;

import java.util.Optional;

public interface CustomerCustomInterface {
    Optional<User> getByIdUser(String idUser);
}
