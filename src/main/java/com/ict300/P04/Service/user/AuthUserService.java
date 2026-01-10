package com.ict300.P04.Service.user;

import com.ict300.P04.DTO.user.request.LoginUserDTO;
import com.ict300.P04.DTO.user.request.RegisterUserDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Utilitaires.GenerateID;
import com.ict300.P04.repository.interfaces.user.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthUserService {
    @Autowired
    private UserInterface userInterface;

    @Autowired
    private GenerateID generateID;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterUserDTO registerUserDTO){
        if(userInterface.existsByName(registerUserDTO.getName())){
            throw new RuntimeException("Nom d'utilisateur deja pris");
        }
        if(Objects.equals(registerUserDTO.getPassword(), registerUserDTO.getPasswordConfirmer())){
            throw new RuntimeException("Le mot de passe doit etre le meme ");
        }
        User newUser = new User();

        newUser.setIdUser(generateID.GenerateUserID());
        newUser.setName(registerUserDTO.getName());
        newUser.setPhone(registerUserDTO.getPhone());
        newUser.setEmail(registerUserDTO.getEmail());
        newUser.setRegistrationDate(LocalDateTime.now());
        newUser.setLastLogin(LocalDateTime.now());
        newUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        newUser.setRole("UTILISATEUR");
        newUser.setStatus("ACTIF");

        userInterface.save(newUser);
    }

    public boolean login(LoginUserDTO loginUserDTO){
        Optional<User> userOpt = userInterface.findByName(loginUserDTO.getUsername());

        if(userOpt.isPresent()) {
            User user = userOpt.get();
            return passwordEncoder.matches(loginUserDTO.getPassword() , user.getPassword());
        }
        return false;
    }
}
