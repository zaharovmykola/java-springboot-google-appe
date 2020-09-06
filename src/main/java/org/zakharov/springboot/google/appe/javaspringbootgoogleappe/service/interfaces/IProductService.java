package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces;

import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductFilterModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;

public interface IProductService {
    ResponseModel create(ProductModel productModel);
    ResponseModel update(ProductModel productModel);
    ResponseModel getAll();
    ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException;
    //ResponseModel getFiltered(ProductFilterModel filter);
    //ResponseModel search(ProductSearchModel searchModel);
}
