package com.ict300.P04.Controller.user.seller;
import com.ict300.P04.DTO.user.customer.response.AuthResponseDTO;
import com.ict300.P04.DTO.user.seller.request.RegisterSellerDTO;
import com.ict300.P04.Service.user.seller.AuthSellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quincaillerie/auth")
@Tag(name = "AuthentificationSeller", description = "l'Inscription et la connexion des utilisateurs(vendeur)")
public class AuthSellerController {
    @Autowired
    private AuthSellerService authSellerService;

    @PostMapping("/registerSeller")
    @Operation(summary = "Inscription vendeur")
    public ResponseEntity<AuthResponseDTO> registerUSer(@Valid @RequestBody RegisterSellerDTO registerSellerDTO) throws Exception {
        System.out.println("vendeur: "+registerSellerDTO.getUser());
        System.out.println("quincaillerie: "+registerSellerDTO.getQuincaillerie());

        Object[] result = authSellerService.registerSeller(registerSellerDTO);
        AuthResponseDTO response = (AuthResponseDTO) result[2];
        return ResponseEntity.ok(response);
    }
}
