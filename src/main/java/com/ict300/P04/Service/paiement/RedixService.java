package com.ict300.P04.Service.paiement;

import com.ict300.P04.DTO.paiement.redix.VendeurContextDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedixService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "retrait:context:";


    public void saveContext(String idCommande, String ip, String userAgent) {
        String key = PREFIX + idCommande;
        VendeurContextDTO context = new VendeurContextDTO(ip, userAgent);
        redisTemplate.opsForValue().set(key, context, Duration.ofHours(24));
    }

    public VendeurContextDTO getContext(String idCommande) {
        String key = PREFIX + idCommande;
        return (VendeurContextDTO) redisTemplate.opsForValue().get(key);
    }


    public void deleteContext(String idCommande) {
        String key = PREFIX + idCommande;
        redisTemplate.delete(key);
    }
}
