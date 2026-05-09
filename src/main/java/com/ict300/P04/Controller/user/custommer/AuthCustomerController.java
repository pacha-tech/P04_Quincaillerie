package com.ict300.P04.Controller.user.custommer;

import com.ict300.P04.DTO.product.request.AddProductDTO;
import com.ict300.P04.DTO.user.customer.request.RegisterCustomerDTO;
import com.ict300.P04.DTO.user.customer.response.AuthResponseDTO;
import com.ict300.P04.Service.user.customer.AuthCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/quincaillerie/auth")
@Tag(name = "AuthentificationUser", description = "l'Inscription et la connexion des utilisateurs(client)")
public class AuthCustomerController {
    @Autowired
    private AuthCustomerService authCustomerService;

    @PostMapping("/registerCustomer")
    @Operation(summary = "Inscription d'un client")
    public ResponseEntity<?> registerUSer(@RequestPart("data") @Valid RegisterCustomerDTO request , @RequestPart(value = "photo" , required = false) MultipartFile photo ) throws Exception {

        Object[] result = authCustomerService.register(request , photo);
        AuthResponseDTO response = (AuthResponseDTO) result[1];
        return ResponseEntity.ok(response);
    }
}
