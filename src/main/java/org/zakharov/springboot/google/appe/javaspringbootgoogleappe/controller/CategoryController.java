package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CategoryModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.CategoryService;


@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public ResponseEntity<ResponseModel> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @PostMapping("/category")
    public ResponseEntity<ResponseModel> create(@RequestBody CategoryModel category) {
        return new ResponseEntity<>(service.create(category), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/category/{id}")
    public ResponseEntity<ResponseModel> update(@PathVariable Long id, @RequestBody CategoryModel category) {
        category.setId(id);
        return new ResponseEntity<>(service.update(category), HttpStatus.OK);
    }

    @DeleteMapping(value = "/category/{id}")
    public ResponseEntity<ResponseModel> deleteCategory(@PathVariable Long id) throws InstantiationException, IllegalAccessException {
        ResponseModel responseModel = service.delete(id);
        System.out.println(responseModel);
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}