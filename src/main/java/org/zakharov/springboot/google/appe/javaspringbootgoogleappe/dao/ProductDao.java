package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import org.springframework.stereotype.Repository;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.predicate.ProductPredicatesBuilder;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductFilterModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductSearchModel;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class ProductDao extends AbstractDAO<ProductModel> {
    // получение из хранилища объекта типа ProductModel по имени категории
    public ProductModel read(String _title) throws Exception {
        return (ProductModel) ObjectifyService.run(
                (Work) () -> ofy().load().type(ProductModel.class)
                        .filter("title", _title) // отобрать только объекты Категория с заданным именем
                        .first() // получить только первый из найденных объектов
                        .now() // выполнить получение одного найденного объекта немедленно
        );
    }

    // получение отфильтрованных и отсортированных объектов моделей товаров
    public List<ProductModel> getFiltered(ProductSearchModel searchModel, Map<String, List<Long>> inMemoryFilterModel) {

        ProductPredicatesBuilder builder = new ProductPredicatesBuilder(inMemoryFilterModel);
        // разбиение значения http-параметра search
        // на отдельные выражения условий фильтрации
        Pattern pattern = Pattern.compile("([\\w]+?)(:|<|>|<:|>:)([\\w\\]\\[\\,]+?);");
        Matcher matcher = pattern.matcher(searchModel.searchString + ";");
        // после разбиения подстроки - свободного запроса
        // получаем множество, каждый элемент которого состоит из трех частей:
        // 1. имя поля для фильтрации;
        // 2. оператор сравнения;
        // 3. значения для сравнения
        while (matcher.find()) {
            // на каждой итерации получаем один элемент - троицу
            // и добавляем строителю для составления полного выражения фильтра
            // (будет содержать условия фильтрации по всем указанным полям)
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        // добавление сортировки и выполнение запроса к хранилищу
        // ofy().clear();
        return builder.build()
                .order(
                        searchModel.sortingDirection == ProductSearchModel.Order.ASC
                                ? searchModel.orderBy
                                : "-" + searchModel.orderBy
                ).list();
    }

    // получение отсортированных объектов моделей товаров без фильтрации
    public List<ProductModel> getSorted(ProductSearchModel searchModel) {
        return ofy().load().type(ProductModel.class)
                .order(
                        searchModel.sortingDirection == ProductSearchModel.Order.ASC
                                ? searchModel.orderBy
                                : "-" + searchModel.orderBy
                ).list();
    }

    public BigDecimal getMin() {
        return ofy().load().type(ProductModel.class)
                .order("price")
                .first()
                .now()
                .getPrice();
    }

    public BigDecimal getMax() {
        return ofy().load().type(ProductModel.class)
                .order("-price")
                .first()
                .now()
                .getPrice();
    }
}
