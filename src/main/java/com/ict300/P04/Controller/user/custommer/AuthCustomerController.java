package com.ict300.P04.Controller.user.custommer;

import com.ict300.P04.DTO.user.customer.request.RegisterCustomerDTO;
import com.ict300.P04.Service.user.customer.AuthCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/quincaillerie/auth")
@Tag(name = "AuthentificationUser", description = "l'Inscription et la connexion des utilisateurs(client)")
public class AuthCustomerController {
    @Autowired
    private AuthCustomerService authCustomerService;

    @PostMapping("/registerCustomer")
    @Operation(summary = "Inscription d'un client")
    public ResponseEntity<?> registerUSer(@Valid @RequestBody RegisterCustomerDTO request) {
        authCustomerService.register(request);
        return ResponseEntity.ok("Incription Reussis du client " + request.getName());
    }

    /*
    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Connexion avec le username et le password")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserDTO loginUserDTO){
        boolean isValid = authUserService.login(loginUserDTO);
        if(isValid){
            return ResponseEntity.ok("Content de vous revoir Mr/Mme "+loginUserDTO.getUsername());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Identifiants incorrects");
    }
     */
}
