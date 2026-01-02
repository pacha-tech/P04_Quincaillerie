package com.ict300.P04.Controller.user.user;

import com.ict300.P04.DTO.user.request.RegisterUserDTO;
import com.ict300.P04.DTO.user.response.getAllUserDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Service.user.RegisterUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/quincaillerie/auth")
public class RegisterUserController {
    @Autowired
    RegisterUser registerUser;

    @PostMapping("/registerUser")
    public ResponseEntity<?> registerUSer(@Valid @RequestBody RegisterUserDTO request) {
        registerUser.register(request);
        return ResponseEntity.ok("Incription Reussis");
    }

    @GetMapping("/getAllUser")
    public List<getAllUserDTO> getAllUsers(){
        return registerUser.getAllUSers();
    }
}
