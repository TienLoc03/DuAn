package com.example.webapp_shop_ecommerce.controller;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import com.example.webapp_shop_ecommerce.dto.request.bill.BillRequest;
import com.example.webapp_shop_ecommerce.dto.response.bill.BillResponse;
import com.example.webapp_shop_ecommerce.entity.Bill;
import com.example.webapp_shop_ecommerce.dto.response.ResponseObject;
import com.example.webapp_shop_ecommerce.service.IBillService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bill")
public class BillController {

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private IBillService billService;


    @GetMapping
    public ResponseEntity<?> findProductAll(
            @RequestParam(value = "page", defaultValue = "-1") Integer page,
            @RequestParam(value = "size", defaultValue = "-1") Integer size) {
        Pageable pageable = Pageable.unpaged();
        if (size < 0) {
            size = 5;
        }
        if (page >= 0) {
            pageable = PageRequest.of(page, size);
        }
        List<Bill> lstPro = billService.findAllDeletedFalse(pageable).getContent();
        List<BillResponse> lst = lstPro.stream().map(entity -> mapper.map(entity, BillResponse.class)).collect(Collectors.toList());
        return new ResponseEntity<>(lst, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> findObjById(@PathVariable("id") Long id) {
        Optional<Bill> otp = billService.findById(id);
        if (otp.isEmpty()) {
            return new ResponseEntity<>(new ResponseObject("Fail", "Không tìm thấy id " + id, 1, null), HttpStatus.BAD_REQUEST);
        }
        BillResponse bill = otp.map(pro -> mapper.map(pro, BillResponse.class)).orElseThrow(IllegalArgumentException::new);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }
    @PostMapping()
    public ResponseEntity<ResponseObject> saveProduct(@RequestBody BillRequest billDto){
        return billService.createNew(mapper.map(billDto, Bill.class));
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellProducts(@RequestBody BillRequest billRequest) {
        Bill bill = mapper.map(billRequest, Bill.class);
        billService.createNew(bill);
        return new ResponseEntity<>("Bill created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}/bills")
    public ResponseEntity<?> getUserBills(@PathVariable Long userId) {
        List<Bill> userBills = billService.findBillsByUserId(userId);
        List<BillResponse> billResponses = userBills.stream()
                .map(bill -> mapper.map(bill, BillResponse.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(billResponses, HttpStatus.OK);
    }
    @GetMapping("/user/{userId}/bills/pdf")
    public ResponseEntity<byte[]> getUserBillsAsPdf(@PathVariable Long userId) throws IOException {
        List<Bill> userBills = billService.findBillsByUserId(userId);

        // create new document
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // create new page in document
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // append data in bill for document
        int yPosition = 700; // location first
        for (Bill bill : userBills) {
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, yPosition);
            contentStream.showText("Code Bill: " + bill.getCodeBill());
            contentStream.newLine();
            // append data here
            contentStream.endText();
            yPosition -= 20; // change location y for bill
        }

        contentStream.close();

        // covert file PDF to byte arrays
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        byte[] bytes = outputStream.toByteArray();

        // setup header reponse
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "bills.pdf");

        //return data type by array
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
    @GetMapping("/user/{userId}/bills/excel")
    public ResponseEntity<byte[]> getUserBillsAsExcel(@PathVariable Long userId) throws IOException {
        List<Bill> userBills = billService.findBillsByUserId(userId);

        // create new workbook Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bills");

        // create header
        Row headerRow = sheet.createRow(0);
        String[] columns = {"Code Bill", "Bill Type", "Cash", "Digital Currency", "Total Money", "Into Money", "Status", "Booking Date", "Payment Date", "Delivery Date", "Completion Date", "Receiver Name", "Receiver Phone", "Receiver Address"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // add data to excel
        int rowNum = 1;
        for (Bill bill : userBills) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(bill.getCodeBill());
            row.createCell(1).setCellValue(bill.getBillType());
            row.createCell(2).setCellValue(bill.getCash().doubleValue());
            row.createCell(3).setCellValue(bill.getDigitalCurrency().doubleValue());
            row.createCell(4).setCellValue(bill.getTotalMoney().doubleValue());
            row.createCell(5).setCellValue(bill.getIntoMoney().doubleValue());
            row.createCell(6).setCellValue(bill.getStatus());
            // Thêm dữ liệu từ các trường khác của hoá đơn vào đây
        }

        // read workbook to ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // convert ByteArrayOutputStream to byte
        byte[] bytes = outputStream.toByteArray();

        // setup response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "bills.xlsx");

        // return response
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct( @PathVariable("id") Long id){
        System.out.println("Delete ID: " + id);
        return billService.delete(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateProduct(@RequestBody BillRequest billDto, @PathVariable("id") Long id){
        System.out.println("Update ID: " + id);
        Bill bill = null;
        Optional<Bill>  otp = billService.findById(id);
        if (otp.isEmpty()){
            return new ResponseEntity<>(new ResponseObject("Fail", "Không Thấy ID", 1, billDto), HttpStatus.BAD_REQUEST);
        }

        if (otp.isPresent()){
            bill = billService.findById(id).orElseThrow(IllegalArgumentException::new);
            bill = mapper.map(billDto, Bill.class);
            bill.setId(id);
            return billService.update(bill);
        }
        return new ResponseEntity<>(new ResponseObject("Fail", "Không Thế Update", 1, billDto), HttpStatus.BAD_REQUEST);

    }
}
