package com.ict300.P04.DTO.user.customer.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginCustomerDTO {
    @NotBlank(message = "Le Nom est obligatoire")
    private String username;

    @Size(min = 8 ,message = "Le mot de passe doit avoir minimum 8 caracteres")
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    public LoginCustomerDTO(String username , String password){
        this.username = username;
        this.password = password;
    }

    public LoginCustomerDTO(){};
}
