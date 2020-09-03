package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.CategoryDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CategoryModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces.ICategoryService;

@Service
public class CategoryService implements ICategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public ResponseModel create(CategoryModel categoryModel) {
        categoryDao.create(categoryModel);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message(String.format("Category %s Created", categoryModel.getName()))
                .build();
    }

    @Override
    public ResponseModel update(CategoryModel categoryModel) {
        categoryDao.update(categoryModel);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message(String.format("Category %s Updated", categoryModel.getName()))
                .build();
    }

    @Override
    public ResponseModel getAll() {
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(categoryDao.read())
                .build();
    }

    @Override
    public ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException {
        CategoryModel categoryModel = categoryDao.read(id);
        if (categoryModel != null){
            categoryDao.delete(categoryModel.getId());
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(String.format("Category #%s Deleted", categoryModel.getName()))
                    .build();
        } else {
            return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("Category #%d Not Found", id))
                    .build();
        }
    }
}