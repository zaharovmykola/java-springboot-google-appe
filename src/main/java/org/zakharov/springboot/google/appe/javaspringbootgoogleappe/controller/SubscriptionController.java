package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.SubscriptionModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.SubscriptionObjectifyService;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class SubscriptionController {

    @Autowired
    private SubscriptionObjectifyService service;

    @GetMapping("/category/{categoryId}/subscriptions")
    public ResponseEntity<ResponseModel> getAll(@PathVariable Long categoryId) throws Exception {
        return new ResponseEntity<>(service.getByCategoryId(categoryId), HttpStatus.OK);
    }

    @PostMapping("/subscription")
    public ResponseEntity<ResponseModel> create(@RequestBody SubscriptionModel subscription, HttpSession httpSession) {
        subscription.setSubscriberId((Long)httpSession.getAttribute("user_id"));
        return new ResponseEntity<>(service.create(subscription), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/subscriber/{subscriberId}/category/{categoryId}")
    public ResponseEntity<ResponseModel> delete(@PathVariable Long subscriberId, @PathVariable Long categoryId) throws Exception {
        ResponseModel responseModel = service.delete(categoryId, subscriberId);
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
