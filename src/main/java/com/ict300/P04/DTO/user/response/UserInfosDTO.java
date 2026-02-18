package com.ict300.P04.DTO.user.response;

import lombok.Data;

@Data
public class UserInfosDTO {
    private String name;
    private String phone;
    private String photoUrl;

    public UserInfosDTO(String name , String phone , String photoUrl){
        this.name = name;
        this.phone = phone;
        this.photoUrl = photoUrl;
    }

    public UserInfosDTO(){}
}
