package com.ict300.P04.DTO.user.customer.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCustomerDTO {

    @NotBlank(message = "Le Nom est obligatoire")
    private String name;

    @NotBlank(message = "Le Numero de telephone est obligatoire")
    private String phone;

    @Email(message = "l'Email doit etre valide")
    @NotBlank(message = "l'Email est obligatoire")
    private String email;

    @NotBlank(message = "Le password est obligatoire")
    @Size(min = 6, message = "Le password doit faire minimum 6 caractères")
    private String password;

    private String role;
    private String imageUrl;

    public RegisterCustomerDTO(String name , String phone , String email , String password , String role , String imageUrl){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.imageUrl = imageUrl;
    }

    public RegisterCustomerDTO(){}
}
