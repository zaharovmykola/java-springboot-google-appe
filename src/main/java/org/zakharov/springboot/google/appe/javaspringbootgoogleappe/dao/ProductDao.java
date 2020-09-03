package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
import org.springframework.stereotype.Repository;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;


import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class ProductDao extends AbstractDAO<ProductModel> {
    // получение из хранилища объекта типа ProductModel по имени товара
    public ProductModel read(String _name) throws Exception {
        return (ProductModel) ObjectifyService.run(
                (Work) () -> ofy().load().type(ProductModel.class)
                        .filter("name", _name) // отобрать только объекты Товар с заданным именем
                        .first() // получить только первый из найденных объектов
                        .now() // выполнить получение одного найденного объекта немедленно
        );
    }
}
