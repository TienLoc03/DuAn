package com.example.webapp_shop_ecommerce.controller;

import com.example.webapp_shop_ecommerce.entity.*;
import com.example.webapp_shop_ecommerce.service.IBillService;
import com.example.webapp_shop_ecommerce.service.Impl.BillServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class SellAtTheCouterResource {
    @Autowired
    private IBillService iBillService;
    @Autowired
    private BillServiceImpl billService;

}
