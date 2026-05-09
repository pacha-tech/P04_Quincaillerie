package com.ict300.P04.repository.interfaces.retraitCode;

import com.ict300.P04.Entite.RetraitCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetraitCodeInterface extends JpaRepository<RetraitCode , String> , RetraitCodeCustomInterface {
}
