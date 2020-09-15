package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.appengine.repackaged.com.google.gson.annotations.Expose;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity(name = "CartItems")
public class CartItemModel {
    @Id
    private Long id;
    @Index
    private String name;
    @Index
    private Integer count;
    @Index
    private Double price;
    @Index
    public enum Action {
        ADD // добавить один пункт товара в корзину
        , SUB // убрать один пункт товара из корзины
        , REM // убрать все пункты товара из корзины
    }
}