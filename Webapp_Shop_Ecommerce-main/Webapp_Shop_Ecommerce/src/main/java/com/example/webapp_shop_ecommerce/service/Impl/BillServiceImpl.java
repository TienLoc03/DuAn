package com.example.webapp_shop_ecommerce.service.Impl;

import com.example.webapp_shop_ecommerce.entity.Bill;
import com.example.webapp_shop_ecommerce.entity.BillDetails;
import com.example.webapp_shop_ecommerce.repositories.IBillRepository;
import com.example.webapp_shop_ecommerce.service.IBillService;
import com.example.webapp_shop_ecommerce.service.ICustomerService;
import com.example.webapp_shop_ecommerce.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class BillServiceImpl extends BaseServiceImpl<Bill, Long, IBillRepository> implements IBillService {
    @Autowired
    private IBillRepository billRepository;


    @Override
    public List<Bill> findBillsByUserId(Long userId) {
        return billRepository.findByUser_Id(userId);
    }
}
