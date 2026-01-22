package com.ict300.P04.repository.interfaces.quincaillerie;

import com.ict300.P04.Entite.Quincaillerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuincaillerieInterface extends JpaRepository<Quincaillerie , String> , QuincaillerieCustomInterface {
}
