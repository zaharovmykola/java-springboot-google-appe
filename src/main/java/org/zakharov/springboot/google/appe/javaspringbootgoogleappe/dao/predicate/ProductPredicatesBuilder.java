package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.googlecode.objectify.cmd.Query;
import javafx.beans.binding.BooleanExpression;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.QueryBox;
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
    private List<SearchCriteria> inMemoryFilterList;

    public ProductPredicatesBuilder(List<SearchCriteria> inMemoryFilterList) {
        this.params = new ArrayList<>();
        this.inMemoryFilterList = inMemoryFilterList;
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
        QueryBox queryBox = new QueryBox();
        queryBox.query = query;
        // на основе каждой структуры входных данных
        // формируем предикат,
        // отсеиваем только успешно созданные предикаты,
        // отбрасывая возможные выходные элементы потока со значением null
        params.forEach(searchCriteria -> {
            ProductPredicate predicate = new ProductPredicate(searchCriteria, queryBox.previousSearchCriteria);
            try {
                // в каждый вызов метода addPredicate передаем ссылку на объект запроса,
                // а также ссылку на список SearchCriteria, отложенных для выполнения
                // фильтрации в памяти
                queryBox.query = predicate.addPredicate(queryBox.query, inMemoryFilterList);
                queryBox.previousSearchCriteria = searchCriteria;
                System.out.println(inMemoryFilterList);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        // собираем список предикатов в результирующее составное условие
        // отбора результатов запроса к источнику данных
        return queryBox.query;
    }
}
