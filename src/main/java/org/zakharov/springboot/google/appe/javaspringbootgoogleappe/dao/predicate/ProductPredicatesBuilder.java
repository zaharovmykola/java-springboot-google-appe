package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.googlecode.objectify.cmd.Query;
import javafx.beans.binding.BooleanExpression;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.criteria.SearchCriteria;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.googlecode.objectify.ObjectifyService.ofy;

/*
 * Строитель составного выражения фильтрации запрашиваемых данных
 * из набора предикатов
 * */
public class ProductPredicatesBuilder {

    private List<SearchCriteria> params;
    private Map<String, List<Long>> inMemoryFilterModel;

    public ProductPredicatesBuilder(Map<String, List<Long>> inMemoryFilterModel) {
        this.params = new ArrayList<>();
        this.inMemoryFilterModel = inMemoryFilterModel;
    }

    // обеспечение возможности добавления предикатов в запрос query dsl
    // цепочкой вызовов метода with,
    // принимающего три составляющие для каждого предиката
    // согласно структуры исходных данных SearchCriteria
    public ProductPredicatesBuilder with(
            String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    // построение запроса из всей цепочки предикатов
    public Query<ProductModel> build() {

        if (params.size() == 0) {
            return null;
        }
        // строим основу запроса на чтение данных из хранилища:
        // объекты типа ProductModel
        Query<ProductModel> query =
                ofy().load().type(ProductModel.class);
        // на основе каждой структуры входных данных
        // формируем предикат,
        // отсеиваем только успешно созданные предикаты,
        // отбрасывая возможные выходные элементы потока со значением null
        params.forEach(searchCriteria -> {
            ProductPredicate predicate = new ProductPredicate(searchCriteria);
            try {
                predicate.getPredicate(query, inMemoryFilterModel);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        // собираем список предикатов в результирующее составное условие
        // отбора результатов запроса к источнику данных
        return query;
    }
}
