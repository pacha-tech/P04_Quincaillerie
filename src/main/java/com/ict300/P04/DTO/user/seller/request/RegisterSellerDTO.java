package com.ict300.P04.DTO.user.seller.request;

import com.ict300.P04.DTO.quincaillerie.request.RegisterQuincaillerieDTO;
import com.ict300.P04.DTO.user.customer.request.RegisterCustomerDTO;
import lombok.Data;

@Data
public class RegisterSellerDTO {
    private RegisterCustomerDTO customer;
    private RegisterQuincaillerieDTO quincaillerie;

    public RegisterSellerDTO(RegisterCustomerDTO customer , RegisterQuincaillerieDTO quincaillerie){
        this.customer = customer;
        this.quincaillerie = quincaillerie;
    }

    public RegisterSellerDTO(){}
}
