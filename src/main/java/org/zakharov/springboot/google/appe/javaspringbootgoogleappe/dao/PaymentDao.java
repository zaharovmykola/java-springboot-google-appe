package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
import org.springframework.stereotype.Repository;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.PaymentModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class PaymentDao extends AbstractDAO<PaymentModel> {

    // получение из хранилища объекта типа PaymentModel по имени vendor
    public List<PaymentModel> readByVendor(String _vendor) throws Exception {
        return (List<PaymentModel>) ObjectifyService.run(
                (Work) () -> ofy().load().type(PaymentModel.class)
                        .filter("vendor", _vendor) // отобрать только объекты Категория с заданным именем
                        .first() // получить только первый из найденных объектов
                        .now() // выполнить получение одного найденного объекта немедленно
        );
    }
}
