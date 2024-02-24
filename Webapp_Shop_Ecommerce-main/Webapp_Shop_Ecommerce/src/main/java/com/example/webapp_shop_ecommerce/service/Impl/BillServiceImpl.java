package com.example.webapp_shop_ecommerce.service.Impl;

import com.example.webapp_shop_ecommerce.entity.Bill;
import com.example.webapp_shop_ecommerce.entity.BillDetails;
import com.example.webapp_shop_ecommerce.repositories.IBillRepository;
import com.example.webapp_shop_ecommerce.service.IBillService;
import com.example.webapp_shop_ecommerce.service.ICustomerService;
import com.example.webapp_shop_ecommerce.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class BillServiceImpl extends BaseServiceImpl<Bill, Long, IBillRepository> implements IBillService {
    @Autowired
    private IBillRepository billRepository;




    @Autowired
    private IProductService productService;
    public Bill confirmPayment(Long billId) {
        Optional<Bill> optionalBill = billRepository.findById(billId);
        if (optionalBill.isPresent()) {
            Bill bill = optionalBill.get();
            if (!bill.getStatus().equals("confirmed")) { //Check to see if the order has been confirmed for payment
                bill.setStatus("confirmed"); // Update the order status to payment confirmed
                billRepository.save(bill); // Save changes to the database
            }
            return bill;
        } else {
            throw new RuntimeException("Bill not found with id: " + billId);
        }
    }

    public String printBill(Long billId) {
        Optional<Bill> optionalBill = billRepository.findById(billId);
        if (optionalBill.isPresent()) {
            Bill bill = optionalBill.get();
            // Create a plain text string representing invoice information
            StringBuilder billPrint = new StringBuilder();
            billPrint.append("Bill ID: ").append(bill.getId()).append("\n");
            billPrint.append("Customer: ").append(bill.getCustomer().getFullName()).append("\n");
            billPrint.append("Total Amount: ").append(bill.getTotalMoney()).append("\n");
            // Add other information depending on your needs
            return billPrint.toString();
        } else {
            throw new RuntimeException("Bill not found with id: " + billId);
        }
    }
    public Bill addBill(Bill bill) {
        return billRepository.save(bill);
    }

    //solution , without testing the interface
    /*
     public Bill addBill(BillDTO billDTO) {
        // Create a Bill object from data in BillDTO
        Bill bill = new Bill();
        bill.setCodeBill(generateBillCode()); // Phương thức này để tạo mã đơn hàng, bạn có thể triển khai nó tùy ý
        bill.setCustomer(customerService.addOrUpdateCustomer(billDTO.getCustomer())); // Thêm hoặc cập nhật thông tin khách hàng
        bill.setBookingDate(new Date()); // Đặt ngày đặt hàng là ngày hiện tại

        // Create a list of line items from product information in BillDTO
        List<BillDetails> billDetailsList = new ArrayList<>();
        for (BillProductDTO productDTO : billDTO.getProducts()) {
            BillDetails billDetails = new BillDetails();
            billDetails.setBill(bill);
            billDetails.setProductDetails(productService.getProductDetailsById(productDTO.getProductId()));
            billDetails.setQuantity(productDTO.getQuantity());
            billDetails.setUnitPrice(productDTO.getUnitPrice());
            billDetailsList.add(billDetails);
        }
        bill.setBillDetailsList(billDetailsList);

        // Calculate the total amount of the order
        BigDecimal totalMoney = calculateTotalMoney(billDetailsList);
        bill.setTotalMoney(totalMoney);

        // Save orders to database
        return billRepository.save(bill);
    }
    * */
}
