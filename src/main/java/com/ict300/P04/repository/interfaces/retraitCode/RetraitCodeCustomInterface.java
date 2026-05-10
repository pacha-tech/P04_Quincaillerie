package com.ict300.P04.repository.interfaces.retraitCode;

import com.ict300.P04.Entite.RetraitCode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RetraitCodeCustomInterface {
    Optional<RetraitCode> findByCommandeId(String idCommande);
    void deleteOldCodes(Instant limit);
}
