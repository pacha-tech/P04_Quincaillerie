package com.ict300.P04.Service.user;

import com.ict300.P04.DTO.user.response.UserInfosDTO;
import com.ict300.P04.Entite.User;
import com.ict300.P04.repository.interfaces.user.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserInterface userInterface;

    //fonction pour recuperer les infos d'un user
    public UserInfosDTO getUserInfo(String id_user){
        User user = userInterface.getUser(id_user);
        UserInfosDTO userInfosDTO = new UserInfosDTO();

        userInfosDTO.setName(user.getName());
        userInfosDTO.setPhone(user.getPhone());
        userInfosDTO.setPhotoUrl(user.getPhotoUrl());

        return userInfosDTO;
    }
}
