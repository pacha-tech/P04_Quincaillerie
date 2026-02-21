package com.ict300.P04.Service.user.seller;


import com.ict300.P04.DTO.user.seller.request.RegisterSellerDTO;
import com.ict300.P04.Service.quincaillerie.QuincaillerieService;
import com.ict300.P04.Service.user.customer.AuthCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AuthSellerService {

    @Autowired
    private AuthCustomerService authCustomerService;

    @Autowired
    private QuincaillerieService quincaillerieService;

    @Transactional
    public void registerSeller(RegisterSellerDTO registerSellerDTO){
        authCustomerService.register(registerSellerDTO.getUser());
        quincaillerieService.registerQuincaillerie(registerSellerDTO.getQuincaillerie());
    }
}
