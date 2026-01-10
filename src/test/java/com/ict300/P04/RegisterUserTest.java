
/*
package com.ict300.P04;

import com.ict300.P04.Controller.user.user.RegisterUserController;
import com.ict300.P04.DTO.user.request.RegisterUserDTO;
import com.ict300.P04.Service.user.RegisterUserService;
import com.ict300.P04.repository.interfaces.favoriteQuincaillerie.FavoriteQuincaillerieInterface;
import com.ict300.P04.repository.interfaces.price.PriceInterface;
import com.ict300.P04.repository.interfaces.user.UserInterface;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RegisterUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RegisterUserTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserService registerUserService;

    @MockitoBean
    private PriceInterface priceInterface;

    @MockitoBean
    private FavoriteQuincaillerieInterface favoriteQuincaillerieInterface;

    @MockitoBean
    UserInterface userInterface;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_ShouldReturn200_WhenRequestIsValid() throws Exception {

        RegisterUserDTO request = new RegisterUserDTO("pacha", "65784521" , "pacha@mail.com" , "Pachar12547" , "ADMIN");

        mockMvc.perform(post("/quincaillerie/auth/registerUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(content().string("Inscription Reussis"));
    }
}
 */
