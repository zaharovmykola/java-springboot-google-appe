package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces;

import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CategoryModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;

public interface ICategoryService {
    ResponseModel create(CategoryModel categoryModel);
    ResponseModel update(CategoryModel categoryModel);
    ResponseModel getAll();
    ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException;
}
