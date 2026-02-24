package com.ict300.P04.Service.user.seller;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.ict300.P04.DTO.user.seller.request.RegisterSellerDTO;
import com.ict300.P04.Entite.Quincaillerie;
import com.ict300.P04.Service.quincaillerie.QuincaillerieService;
import com.ict300.P04.Service.user.customer.AuthCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthSellerService {

    @Autowired
    private AuthCustomerService authCustomerService;

    @Autowired
    private QuincaillerieService quincaillerieService;

    @Transactional
    public void registerSeller(RegisterSellerDTO registerSellerDTO){
        authCustomerService.register(registerSellerDTO.getUser());
        Quincaillerie quincaillerie = quincaillerieService.registerQuincaillerie(registerSellerDTO.getQuincaillerie());


        Map<String, Object> claims = new HashMap<>();
        claims.put("role", registerSellerDTO.getUser().getRole());
        claims.put("quincaillerieId", quincaillerie.getIdQuincaillerie());

        try {
            FirebaseAuth.getInstance().setCustomUserClaims(registerSellerDTO.getUser().getId_user(), claims);
            System.out.println("✅ Rôle " + registerSellerDTO.getUser().getRole() + "et "+quincaillerie.getIdQuincaillerie()+" ajouté au Token Firebase"+registerSellerDTO.getUser().getId_user());
        } catch (FirebaseAuthException e) {
            System.err.println("❌ Erreur Claims du vendeur : " + e.getMessage());
        }

    }
}
