package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.predicate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.googlecode.objectify.cmd.Query;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.criteria.SearchCriteria;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;

import static com.googlecode.objectify.ObjectifyService.ofy;

/* Динамически создаваемый предикат для поисковых запросов query dsl */
public class ProductPredicate {

    // входящие данные для построения предиката
    private SearchCriteria criteria;

    public ProductPredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public Query<ProductModel> getPredicate(Query<ProductModel> query) throws JsonProcessingException {
        ofy().clear();
        if (StringUtils.isNumeric(criteria.getValue().toString())) {
            int value = Integer.parseInt(criteria.getValue().toString());
            // возвращается выражение сравнения чисел
            switch (criteria.getOperation()) {
                // значение поля должно быть строго равно значению value
                case ":":
                    return query.filter(criteria.getKey(), value);
                // значение поля должно быть больше значения value, исключая его
                case ">":
                    return query.filter(criteria.getKey() + " >", value);
                // значение поля должно быть меньше значения value, исключая его
                case "<":
                    return query.filter(criteria.getKey() + " <", value);
                // значение поля должно быть больше значения value, исключая его
                case ">:":
                    return query.filter(criteria.getKey() + " >=", value);
                // значение поля должно быть меньше значения value, исключая его
                case "<:":
                    final int SQL_ROUND_COMPENSATION = 1;
                    return query.filter(criteria.getKey() + " <=", value + SQL_ROUND_COMPENSATION);
            }
        }/* else if (criteria.getValue().toString().startsWith("[")) {
            // если первый символ значения - открывающая квадратная скобка массива -
            // меняем объект строитель так, чтобы он работал с сущностью Товар,
            // но строил предикат относительно идентификатора,
            // содержащегося в заданном поле сущности товар
            // (например, идентификатор категории, к которой относится товар)
            entityPath =
                new PathBuilder<>(Product.class, criteria.getKey() + ".id");
            // строковое значение формата json array преобразовываем в список чисел
            List value =
                Arrays.asList(
                    new ObjectMapper().readValue(
                        criteria.getValue().toString(),
                        Long[].class
                    )
                );
            //
            return entityPath.in(value);
        } */else {
            // если полученное значение - не число и не множество -
            // строим предикат строгого сравнения строк
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return query.filter(criteria.getKey(), criteria.getValue().toString());
            }
        }
        return null;
    }
}
