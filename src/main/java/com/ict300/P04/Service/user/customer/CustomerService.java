package com.ict300.P04.Service.user.customer;

import com.ict300.P04.DTO.user.customer.response.UserInfosDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.Exception.UserNotFoundException;
import com.ict300.P04.repository.interfaces.user.customer.CustomerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    private CustomerInterface userInterface;

    //fonction pour recuperer les infos d'un user
    public UserInfosDTO getUserInfo(String id_user){
        User user = userInterface.getByIdUser(id_user).orElse(null);
        if(user == null){
            throw new UserNotFoundException("L'utilisateur n'existe Pas");
        }
        UserInfosDTO userInfosDTO = new UserInfosDTO();

        userInfosDTO.setName(user.getName());
        userInfosDTO.setPhone(user.getPhone());
        userInfosDTO.setPhotoUrl(user.getPhotoUrl());
        userInfosDTO.setRole(user.getRole());
        userInfosDTO.setEmail(user.getEmail());

        return userInfosDTO;
    }
}
