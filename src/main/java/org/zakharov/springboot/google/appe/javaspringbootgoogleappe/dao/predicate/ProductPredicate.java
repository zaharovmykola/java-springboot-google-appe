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
    // прошлые входящие данные, использованные для построения предыдущего предиката
    // (нужны для проверки: происходит ли при очередном вызове метода addPredicate
    // попытка добавить к запросу фильтр с тем же именем поля, что и при предыдущем вызове,
    // а проверка нужна для того, чтобы определить, можно ли добавлять новый фильтр в запрос:
    // если имена предыдущего и текущего полей совпадут - то можно)
    private SearchCriteria previousCriteria;

    // признак доступности фильтрации в хранилище
    // (максимум - по одному полю, один или более фильтров)
    // true - квота не исчерпана
    // false - квота исчерпана
    public static Boolean filtersQuota = true;

    public ProductPredicate(SearchCriteria criteria, SearchCriteria previousCriteria) {
        this.criteria = criteria;
        this.previousCriteria = previousCriteria;
    }

    public Query<ProductModel> addPredicate(Query<ProductModel> query, List<SearchCriteria> inMemoryFilterList) throws JsonProcessingException {
        System.out.println(criteria.getKey().equals(previousCriteria.getKey()));
        System.out.println(ProductPredicate.filtersQuota == true);
        if (criteria.getKey().equals(previousCriteria.getKey())
                || ProductPredicate.filtersQuota == true) {
            String valueString = criteria.getValue().toString();
            if (StringUtils.isNumeric(valueString)) {
                // если в хранилище находятся числа дробного типа,
                // а в качестве значения в фильтре указано целое число,
                // такой фильтр не работает
                Number value;
                // если имя поля - price,
                // то конвертируем значение из строки в дробное число
                if (criteria.getKey().equals("price")) {
                    value = Double.parseDouble(valueString);
                } else {
                    // ... иначе - в целое
                    value = Integer.parseInt(valueString);
                }
                // int value = Integer.parseInt(valueString);
                // возвращается выражение сравнения чисел
                System.out.println("predicate: ");
                System.out.println(criteria.getKey());
                System.out.println(criteria.getOperation());
                System.out.println(value);
                query = query.order(criteria.getKey());
                switch (criteria.getOperation()) {
                    // значение поля должно быть строго равно значению value
                    case ":":
                        query = query.filter(criteria.getKey(), value);
                        break;
                    // значение поля должно быть больше значения value, исключая его
                    case ">":
                        System.out.println(criteria.getOperation());
                        query = query.filter(criteria.getKey() + " >", value);
                        break;
                    // значение поля должно быть меньше значения value, исключая его
                    case "<":
                        System.out.println(criteria.getOperation());
                        query = query.filter(criteria.getKey() + " <", value);
                        break;
                    // значение поля должно быть больше значения value, исключая его
                    case ">:":
                        query = query.filter(criteria.getKey() + " >=", value);
                        break;
                    // значение поля должно быть меньше значения value, исключая его
                    case "<:":
                        // final int SQL_ROUND_COMPENSATION = 1;
                        // query = query.filter(criteria.getKey() + " <=", value + SQL_ROUND_COMPENSATION);
                        query = query.filter(criteria.getKey() + " <=", value);
                        break;
                }
                ProductPredicate.filtersQuota = false;
            } else if (valueString.startsWith("[")) {
                inMemoryFilterList.add(criteria);
            } else {
                // если полученное значение - не число и не множество -
                // строим предикат строгого сравнения строк
                if (criteria.getOperation().equalsIgnoreCase(":")) {
                    query = query.filter(criteria.getKey(), valueString);
                    ProductPredicate.filtersQuota = false;
                }
            }
        } else {
            String valueString = criteria.getValue().toString();
            // если значение - не число и не массив,
            // то можно добавить фильтр строгого сравления в запрос,
            // несмотря на то, что это не единственный фильтр
            // (ограничение фильтрации только по одному полю действует
            // только на выражения с опреаторами неравенства)
            System.out.println("to im start");
            System.out.println(!StringUtils.isNumeric(valueString));
            System.out.println(!valueString.startsWith("["));
            System.out.println("to im end");
            // если значение - не число и не массив,
            // то добавляем фильтр строгого равенства строк
            if(!StringUtils.isNumeric(valueString) && !valueString.startsWith("[")) {
                if (criteria.getOperation().equalsIgnoreCase(":")) {
                    query = query.filter(criteria.getKey(), valueString);
                }
            } else {
                // иначе - добавляем текущую модель criteria в отложденный список
                // для дальнейшей фильтрации в памяти
                inMemoryFilterList.add(criteria);
            }
        }
        return query;
    }
}
