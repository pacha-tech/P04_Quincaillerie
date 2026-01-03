package com.ict300.P04.Controller.user.user;

import com.ict300.P04.DTO.user.request.RegisterUserDTO;
import com.ict300.P04.DTO.user.response.getAllUserDTO;
import com.ict300.P04.Service.user.RegisterUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/quincaillerie/auth")
@Tag(name = "RegisterUser", description = "Inscrire un nouveau utilisateur(client)")
public class RegisterUserController {
    @Autowired
    RegisterUserService registerUser;

    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUSer(@Valid @RequestBody RegisterUserDTO request) {
        registerUser.register(request);
        return ResponseEntity.ok("Incription Reussis");
    }

    /*
    @GetMapping("/getAllUser")
    public List<getAllUserDTO> getAllUsers(){
        return registerUser.getAllUSers();
    }
     */
}
