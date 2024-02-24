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


    @PostMapping("create/bill")
    public ResponseEntity<Bill> addBill(@RequestBody Bill billDTO) {
        Bill bill = billService.addBill(billDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(bill);
    }
    @PutMapping("bills/{billId}/confirm-payment")
    public ResponseEntity<Bill> confirmPayment(@PathVariable Long billId) {
        Bill confirmedBill = billService.confirmPayment(billId);
        return ResponseEntity.ok(confirmedBill);
    }

    @GetMapping("bills/{billId}/print")
    public ResponseEntity<String> printBill(@PathVariable Long billId) {
        String billPrint = billService.printBill(billId);
        return ResponseEntity.ok(billPrint);
    }

}
