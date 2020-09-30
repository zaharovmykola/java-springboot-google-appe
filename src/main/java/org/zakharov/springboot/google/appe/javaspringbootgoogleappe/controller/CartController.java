package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CartItemModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CartModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.CartService;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // внедрение объекта сеанса http через аргумент метода
    @GetMapping("")
    public ResponseEntity<ResponseModel> getCartItems(HttpSession httpSession) {
        CartModel cartModel = (CartModel) httpSession.getAttribute("CART");
        if (cartModel == null) {
            cartModel = new CartModel();
        }
        return new ResponseEntity<>(cartService.getCartItemModels(cartModel), HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ResponseModel> addCartItemCount(@PathVariable("id") Long id, HttpSession httpSession) throws Exception {
        // попытка извлечь из объекта сеанса объект корзины
        CartModel cartModel = (CartModel) httpSession.getAttribute("CART");
        if (cartModel == null) {
            // если не удалось - создаем новый объект корзины
            cartModel = new CartModel();
        }
        // вызов метода службы - увеличить число товара в корзине на 1
        ResponseModel response =
                cartService.changeCartItemModelCount(
                        cartModel
                        , id
                        , CartItemModel.Action.ADD
                );
        // сохранение объекта корзины в сеанс -
        // первичное или обновление
        httpSession.setAttribute("CART", cartModel);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel> subtractCartItemCount(@PathVariable("id") Long id, HttpSession httpSession) throws Exception {
        CartModel cartModel = (CartModel) httpSession.getAttribute("CART");
        if (cartModel == null) {
            cartModel = new CartModel();
        }
        ResponseModel response =
                cartService.changeCartItemModelCount(
                        cartModel
                        , id
                        , CartItemModel.Action.SUB
                );
        httpSession.setAttribute("CART", cartModel);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseModel> deleteCartItem(@PathVariable("id") Long id, HttpSession httpSession) throws Exception {
        CartModel cartModel = (CartModel) httpSession.getAttribute("CART");
        if (cartModel == null) {
            cartModel = new CartModel();
        }
        ResponseModel response =
                cartService.changeCartItemModelCount(
                        cartModel
                        , id
                        , CartItemModel.Action.REM
                );
        httpSession.setAttribute("CART", cartModel);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
