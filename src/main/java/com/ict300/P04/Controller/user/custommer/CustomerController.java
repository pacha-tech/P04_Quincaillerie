package com.ict300.P04.Controller.user.custommer;

import com.ict300.P04.DTO.user.customer.response.UserInfosDTO;
import com.ict300.P04.Service.user.customer.CustomerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/quincaillerie/users")
@Tag(name = "AuthentificationUser", description = "l'Inscription et la connexion des utilisateurs(client)")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {

        String uid = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserInfosDTO userInfosDTO = customerService.getUserInfo(uid);
        if (userInfosDTO != null) {
            System.out.println(userInfosDTO);
            return ResponseEntity.ok(userInfosDTO);
        } else {
            return null;
        }
    }
}

