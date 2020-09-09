package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.cmd.Query;
import org.apache.commons.lang3.StringUtils;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.criteria.SearchCriteria;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/* Динамически создаваемый предикат для поисковых запросов query dsl */
public class ProductPredicate {

    // входящие данные для построения предиката
    private SearchCriteria criteria;

    public ProductPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public void getPredicate(Query<ProductModel> query, Map<String, List<Long>> inMemoryFilterModel) throws JsonProcessingException {
        ofy().clear();
        if (StringUtils.isNumeric(criteria.getValue().toString())) {
            int value = Integer.parseInt(criteria.getValue().toString());
            // возвращается выражение сравнения чисел
            switch (criteria.getOperation()) {
                // значение поля должно быть строго равно значению value
                case ":":
                    query = query.filter(criteria.getKey(), value);
                    // значение поля должно быть больше значения value, исключая его
                case ">":
                    query = query.filter(criteria.getKey() + " >", value);
                    // значение поля должно быть меньше значения value, исключая его
                case "<":
                    query = query.filter(criteria.getKey() + " <", value);
                    // значение поля должно быть больше значения value, исключая его
                case ">:":
                    query = query.filter(criteria.getKey() + " >=", value);
                    // значение поля должно быть меньше значения value, исключая его
                case "<:":
                    final int SQL_ROUND_COMPENSATION = 1;
                    query = query.filter(criteria.getKey() + " <=", value + SQL_ROUND_COMPENSATION);
            }
        } else if (criteria.getValue().toString().startsWith("[")) {
            // если первый символ значения - открывающая квадратная скобка массива -
            // добавляем в  выходной параметр модель заказа на фильтрацию в памяти
            // строковое значение формата json array преобразовываем в список чисел
            List<Long> value =
                    Arrays.asList(
                            new ObjectMapper().readValue(
                                    criteria.getValue().toString(),
                                    Long[].class
                            )
                    );
            //
            inMemoryFilterModel.put(criteria.getKey(), value);
        } else {
            // если полученное значение - не число и не множество -
            // строим предикат строгого сравнения строк
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                query = query.filter(criteria.getKey(), criteria.getValue().toString());
            }
        }
    }
}
