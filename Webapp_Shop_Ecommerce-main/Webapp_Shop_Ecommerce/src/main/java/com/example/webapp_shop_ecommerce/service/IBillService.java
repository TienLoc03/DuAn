package com.example.webapp_shop_ecommerce.service;

import com.example.webapp_shop_ecommerce.entity.Bill;

import java.util.List;

public interface IBillService extends IBaseService<Bill, Long> {
    List<Bill> findBillsByUserId(Long userId);

}
