package com.ict300.P04.repository.interfaces.user;

import com.ict300.P04.Entite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserInterface extends JpaRepository<User , String> , UserCustomInterface {
    boolean existsByName(String userName);
    Optional<User> findByName(String userName);
}
