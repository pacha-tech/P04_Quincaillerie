package com.ict300.P04.Service.user.customer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.ict300.P04.DTO.user.customer.request.RegisterCustomerDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthCustomerService {
    @Autowired
    private CustomerInterface userInterface;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterCustomerDTO registerUserDTO) throws Exception {

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(registerUserDTO.getEmail())
                .setPassword(registerUserDTO.getPassword());

        UserRecord userRecord;

        // 1. Création dans Firebase
        try {
            userRecord = FirebaseAuth.getInstance().createUser(request);
        } catch (FirebaseAuthException e) {
            throw new Exception("Erreur Firebase : " + e.getMessage());
        }

        User newUser = new User();

        newUser.setIdUser(userRecord.getUid());
        newUser.setName(registerUserDTO.getName());
        newUser.setPhone(registerUserDTO.getPhone());
        newUser.setEmail(registerUserDTO.getEmail());
        newUser.setRegistrationDate(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());
        String roleSet = registerUserDTO.getRole();
        newUser.setRole(roleSet);
        newUser.setStatus("ACTIF");

        try {
            if(roleSet.equals("CLIENT")){
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", roleSet);

                FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
                System.out.println("✅ Rôle " + registerUserDTO.getRole() + " ajouté au Token Firebase");

            }
            userInterface.save(newUser);
        } catch (Exception e) {
            FirebaseAuth.getInstance().deleteUser(userRecord.getUid());
            throw new Exception("Erreur lors de l'enregistrement local. Compte Firebase annulé : " + e.getMessage());
        }
        return newUser;
    }
}
