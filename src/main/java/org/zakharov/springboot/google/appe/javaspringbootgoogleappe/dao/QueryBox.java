package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao;

import com.googlecode.objectify.cmd.Query;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.criteria.SearchCriteria;

/* Обертка для объекта запроса к хранилищу
 * и для объекта SearchCriteria, который использовался в предыдущий раз -
 * для того, чтобы можно было внутри лямбда-функций менять эти два значения значения */
public class QueryBox {
    public Query query;
    public SearchCriteria previousSearchCriteria = new SearchCriteria("", "", "");
}
