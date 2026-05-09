package com.ict300.P04.DTO.user.customer.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  @AllArgsConstructor @NoArgsConstructor
public class AllUserDTO {

    private  String idUser;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String role;
}
