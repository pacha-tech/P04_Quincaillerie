package com.ict300.P04.DTO.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDTO {

    @NotBlank(message = "Le Nom est obligatoire")
    private String name;

    @NotBlank(message = "Le Numero de telephone est obligatoire")
    private String phone;

    @Email(message = "l'Email doit etre valide")
    @NotBlank(message = "l'Email est obligatoire")
    private String email;

    @Size(min = 8 ,message = "Le mot de passe doit avoir minimum 8 caracteres")
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
    private String passwordConfirmer;

    private String role;
    private String imageUrl;

    public RegisterUserDTO(String name , String phone , String email , String password , String role){
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public RegisterUserDTO(){}
}
