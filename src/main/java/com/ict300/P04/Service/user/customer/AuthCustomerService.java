package com.ict300.P04.Service.user.customer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.ict300.P04.DTO.user.customer.request.RegisterCustomerDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthCustomerService {
    @Autowired
    private CustomerInterface userInterface;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterCustomerDTO registerUserDTO){
        if(userInterface.existsByName(registerUserDTO.getName())){
            throw new RuntimeException("Nom d'utilisateur deja pris");
        }

        User newUser = new User();

        newUser.setIdUser(registerUserDTO.getId_user());
        newUser.setName(registerUserDTO.getName());
        newUser.setPhone(registerUserDTO.getPhone());
        newUser.setEmail(registerUserDTO.getEmail());
        newUser.setRegistrationDate(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());
        String roleSet = (registerUserDTO.getRole() != null) ? registerUserDTO.getRole() : "CLIENT";
        newUser.setRole(roleSet);
        newUser.setStatus("ACTIF");

        userInterface.save(newUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roleSet); // "VENDEUR" ou "CLIENT"

        try {
            FirebaseAuth.getInstance().setCustomUserClaims(registerUserDTO.getId_user(), claims);
            System.out.println("✅ Rôle " + registerUserDTO.getRole() + " ajouté au Token Firebase");
        } catch (FirebaseAuthException e) {
            System.err.println("❌ Erreur Claims : " + e.getMessage());
        }
    }

    /*
    public boolean login(LoginUserDTO loginUserDTO){
        Optional<User> userOpt = userInterface.findByName(loginUserDTO.getUsername());

        if(userOpt.isPresent()) {
            User user = userOpt.get();
        }
        return false;
    }
     */
}
