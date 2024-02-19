package com.example.webapp_shop_ecommerce.dto.request.address;

import com.example.webapp_shop_ecommerce.dto.request.customer.CustomerRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class AddressRequest {
    private Long customer;
    private String name;
}
