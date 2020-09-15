package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.PaymentModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.PaymentResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService service;

    @PostMapping("/payNow")
    public PaymentResponseModel payInstant(@RequestBody PaymentModel paymentModel) {
        return service.pay(paymentModel);
    }

    @GetMapping("/getTransactionsByVendor/{vendor}")
    public PaymentResponseModel getTransaction(@PathVariable String vendor) throws Exception {
        return service.getTx(vendor);
    }
}
