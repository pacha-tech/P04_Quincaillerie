package com.ict300.P04.repository.interfaces.detailRetrait;

import com.ict300.P04.Entite.DetailRetrait;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailRetraitInterface extends JpaRepository<DetailRetrait , String> , DetailRetraitCustomInterface {
}
