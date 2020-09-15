package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.ProductDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CartItemModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CartModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private ProductDao productDAO;

    public ResponseModel getCartItemModels(CartModel cartmodel) {
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message("Cart data fetched successfully")
                .data(cartmodel.getCartItems())
                .build();
    }

    // изменить число определенного товара в объекте корзины
    public ResponseModel changeCartItemModelCount(CartModel cartModel, Long productModelId, CartItemModel.Action action) throws Exception {
        CartItemModel currentCartItem = null;
        // в БД находим описание товара по его ИД
        ProductModel productModel = productDAO.readById(productModelId);
        // в объекте корзины пытаемся найти элемент списка товаров в корзине,
        // у которого ИД описания товара такой же, как заданный для изменения
        List<CartItemModel> currentCartItemList =
                cartModel.getCartItems()
                        .stream()
                        .filter((item) -> item.getId().equals(productModelId))
                        .collect(Collectors.toList());
        // если в корзине уже был хотя бы один такой товар
        if (currentCartItemList.size() > 0) {
            currentCartItem = currentCartItemList.get(0);
        } else {
            // если нет - добавляем товар в корзину с указанием его числа 0
            currentCartItem = new CartItemModel(productModelId, productModel.getTitle(), 0, productModel.getPrice());
            cartModel.getCartItems().add(currentCartItem);
        }
        if (action != null) {
            switch (action) {
                case ADD:
                    // увеличение числа товара в корзтине на 1
                    currentCartItem.setCount(currentCartItem.getCount() + 1);
                    break;
                case SUB:
                    // уменьшение числа товара в корзтине на 1,
                    // но если осталось 0 или меньше - полное удаление товара из корзины
                    currentCartItem.setCount(currentCartItem.getCount() - 1);
                    if (currentCartItem.getCount() <= 0) {
                        cartModel.getCartItems().remove(currentCartItem);
                    }
                    break;
                case REM:
                    // безусловное полное удаление товара из корзины
                    cartModel.getCartItems().remove(currentCartItem);
                    break;
                default:
                    break;
            }
        }
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message("Cart data changed successfully")
                .data(cartModel.getCartItems())
                .build();
    }

}
