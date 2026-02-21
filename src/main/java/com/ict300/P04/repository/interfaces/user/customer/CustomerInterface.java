package com.ict300.P04.repository.interfaces.user.customer;

import com.ict300.P04.Entite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerInterface extends JpaRepository<User , String> , CustomerCustomInterface {
    boolean existsByName(String userName);
    Optional<User> findByName(String userName);
}
