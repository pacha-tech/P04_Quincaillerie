package com.ict300.P04.DTO.user.response;

import lombok.Data;

@Data
public class getAllUserDTO {

    private  String idUser;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String role;

    public getAllUserDTO(String idUser , String name , String phone , String email , String password , String role){
        this.idUser = idUser;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public getAllUserDTO(){}
}
