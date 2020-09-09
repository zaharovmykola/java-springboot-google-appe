package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces;

import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductSearchModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;

public interface IProductService {
    ResponseModel create(ProductModel productModel) throws IllegalAccessException, InstantiationException;
    ResponseModel update(ProductModel productModel) throws IllegalAccessException, InstantiationException;
    ResponseModel getAll();
    ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException;
    ResponseModel search(ProductSearchModel searchModel);
    ResponseModel getProductsPriceBounds();
}
