package com.ict300.P04.repository.interfaces.quincaillerie;

import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Entite.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuincaillerieInterface extends JpaRepository<Quincaillerie , String> , QuincaillerieCustomInterface {
    boolean existsByStoreName(String quincaillerieName);
    Optional<Quincaillerie> findByStoreName(String quincaillerieName);
}
