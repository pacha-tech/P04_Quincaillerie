package com.ict300.P04.DTO.user.customer.response;

import lombok.Data;

@Data
public class UserInfosDTO {
    private String name;
    private String phone;
    private String photoUrl;
    private String role;

    public UserInfosDTO(String name , String phone , String photoUrl , String role ){
        this.name = name;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.role = role;
    }

    public UserInfosDTO(){}
}
