package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.PaymentDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.PaymentModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.PaymentResponseModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentDao dao;

    public PaymentResponseModel pay(PaymentModel paymentModel) {
        dao.create(paymentModel);
        PaymentResponseModel response =
                PaymentResponseModel.builder()
                        .status("success")
                        .message("Payment successfull with amount : " + paymentModel.getQuantity())
                        .build();
        return response;
    }

    public PaymentResponseModel getTx(String vendor) throws Exception {
        List<PaymentModel> payments = dao.readByVendor(vendor);
        List<PaymentModel> paymentModels = payments.stream().map((p) ->
                PaymentModel.builder()
                        .id(p.getId())
                        .transactionId(p.getTransactionId())
                        .vendor(p.getVendor())
                        .paymentDate((new SimpleDateFormat("dd/mm/yyyy HH:mm:ss a")).format(p.getPaymentDate()))
                        .quantity(p.getQuantity())
                        .build()
        ).collect(Collectors.toList());
        return PaymentResponseModel.builder()
                .status("success")
                .payments(paymentModels)
                .build();
    }
}
