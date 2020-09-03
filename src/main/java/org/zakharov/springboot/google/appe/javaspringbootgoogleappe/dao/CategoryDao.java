package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
import org.springframework.stereotype.Repository;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CategoryModel;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class CategoryDao extends AbstractDAO<CategoryModel> {
    // получение из хранилища объекта типа CategoryModel по имени категории
    public CategoryModel read(String _name) throws Exception {
        return (CategoryModel) ObjectifyService.run(
                (Work) () -> ofy().load().type(CategoryModel.class)
                        .filter("name", _name) // отобрать только объекты Категория с заданным именем
                        .first() // получить только первый из найденных объектов
                        .now() // выполнить получение одного найденного объекта немедленно
        );
    }
}