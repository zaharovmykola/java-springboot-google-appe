package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.CategoryDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.ProductDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.criteria.SearchCriteria;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.*;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces.IProductService;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductDao productObjectifyDao;

    @Autowired
    private CategoryDao categoryObjectifyDao;

    private List<ProductModel> products;

    @Override
    public ResponseModel create(ProductModel productModel) throws IllegalAccessException, InstantiationException {
        CategoryModel category
                = categoryObjectifyDao.read(productModel.getCategoryId());
        if(category != null) {
            productObjectifyDao.create(productModel);
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(String.format("Product %s Created", productModel.getTitle()))
                    .build();
        } else {
            return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("Category #%d Not Found", productModel.getCategoryId()))
                    .build();
        }
    }

    @Override
    public ResponseModel update(ProductModel productModel) throws IllegalAccessException, InstantiationException {
        CategoryModel category
                = categoryObjectifyDao.read(productModel.getCategoryId());
        if(category != null){
            productObjectifyDao.create(productModel);
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(String.format("Product %s Updated", category.getName()))
                    .build();
        } else {
            return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("Category #%d Not Found", productModel.getCategoryId()))
                    .build();
        }
    }

    @Override
    public ResponseModel getAll() {
        List<ProductModel> products = productObjectifyDao.read();
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(products)
                .build();
    }

    @Override
    public ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException {
        ProductModel product = productObjectifyDao.read(id);
        if (product != null){
            productObjectifyDao.delete(product.getId());
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(String.format("Product #%s Deleted", product.getTitle()))
                    .build();
        } else {
            return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("Product #%d Not Found", id))
                    .build();
        }
    }

    // поиск отфильтрованного и отсортированного списка товаров
    @Override
    public ResponseModel search(ProductSearchModel searchModel) {
        products = new ArrayList<>();
        if (searchModel.searchString != null && !searchModel.searchString.isEmpty()) {
            List<SearchCriteria> inMemoryFilterList = new ArrayList<>();
            // получаем из репозитория список моделей товаров,
            // отфильтрованных по всем критериям, кроме криериев на вхождение в множество знгачений
            products.addAll(
                    productObjectifyDao.getFiltered(searchModel, inMemoryFilterList)
            );
            System.out.println(inMemoryFilterList.size());
            // System.out.println(products);
            // если есть хотя бы одно множество для фильтрации по нему -
            // осуществляем фильтрацию в памяти:
            // удаляем из списка товаров те, в которых поле категории не встречается
            // один из заданных идентификаторов
            inMemoryFilterList.forEach((criteria) -> {
                System.out.println("im filter start");
                System.out.println(criteria.getKey());
                System.out.println(criteria.getOperation());
                System.out.println(criteria.getValue());
                System.out.println("im filter end");
                if(criteria != null) {
                    // если текщее поле - категория,
                    // то фильтруем список товаров в памяти,
                    // отбрасывая все те, у которых идентификатор категорий
                    //  не совпадает ни с одним из идентификаторов из массива,
                    // полученного из параметра строки веб-запроса
                    if (criteria.getKey().equals("category")){
                        List<Long> value = null;
                        try {
                            // превращение строки, содержащей идентификаторы категорий товаров,
                            // в объект - список числовых значений идентификаторов
                            value =
                                    Arrays.asList(
                                            new ObjectMapper().readValue(
                                                    criteria.getValue().toString(),
                                                    Long[].class
                                            )
                                    );
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        List<Long> finalValue = value;
                        // удаляем из списка товаров все объекты, идентификатор категорий которых
                        // не совпадает ни с одним идентификаторов из массива,
                        // олученного из параметра строки веб-запроса
                        products.removeIf((p) -> {
                            boolean categoryIdAbsents = true;
                            for (Long categoryId : finalValue) {
                                if(p.getCategoryId().equals(categoryId)) {
                                    categoryIdAbsents = false;
                                    break;
                                }
                            }
                            return categoryIdAbsents;
                        });
                    } else {
                        // если на обратку в памяти попал не массив -
                        // применяем обычную фильтрацию в памяти
                        // (пропускаем на выод только те объекты,
                        // которые соответствуют условиям фильтра)
                        products =
                                products.stream()
                                        .filter(p -> {
                                            try {
                                                // получаем отражение поля, заданного текущей критерией,
                                                // для этого получаем отражение типа класса Товар,
                                                // и из него узнаем отражение отдельного поля
                                                Field field = p.getClass().getDeclaredField(criteria.getKey());
                                                field.setAccessible(true);
                                                //System.out.println("im filter start");
                                                //System.out.println(field.get(p));
                                                //System.out.println(criteria.getValue());
                                                //System.out.println("im filter end");
                                                // узнаем значение текущего поля фильтрации из текущего объекта типа Товар
                                                Object value = field.get(p);
                                                if (value instanceof Integer || value instanceof Double) {
                                                    // query = query.order(criteria.getKey());
                                                    switch (criteria.getOperation()) {
                                                        // значение поля должно быть строго равно значению value
                                                        case ":":
                                                            return field.get(p).equals(criteria.getValue());
                                                        // значение поля должно быть больше значения value, исключая его
                                                        case ">":
                                                            return ((Number)field.get(p)).doubleValue() > Double.parseDouble((String) criteria.getValue());
                                                        // значение поля должно быть меньше значения value, исключая его
                                                        case "<":
                                                            return ((Number)field.get(p)).doubleValue() < Double.parseDouble((String) criteria.getValue());
                                                        // значение поля должно быть больше значения value, исключая его
                                                        case ">:":
                                                            return (((Number)field.get(p)).doubleValue() > Double.parseDouble((String) criteria.getValue()))
                                                                    || (((Number)field.get(p)).doubleValue() == Double.parseDouble((String) criteria.getValue()));
                                                        // значение поля должно быть меньше значения value, исключая его
                                                        case "<:":
                                                            return (((Number)field.get(p)).doubleValue() < Double.parseDouble((String) criteria.getValue()))
                                                                    || (((Number)field.get(p)).doubleValue() == Double.parseDouble((String) criteria.getValue()));
                                                    }
                                                } else {
                                                    // если полученное значение - не число и не множество -
                                                    // строим предикат строгого сравнения строк
                                                    if (criteria.getOperation().equalsIgnoreCase(":")) {
                                                        return (field.get(p)).equals(criteria.getValue());
                                                    }
                                                }
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                                return false;
                                            } catch (NoSuchFieldException e) {
                                                e.printStackTrace();
                                                return false;
                                            }
                                            return false;
                                        })
                                        .collect(Collectors.toList());
                    }

                }
            });
        } else {
            products.addAll(productObjectifyDao.getSorted(searchModel));
        }
        products.sort((p1, p2) -> {
            Field field = null;
            try {
                field = p1.getClass().getDeclaredField(searchModel.orderBy);
                field.setAccessible(true);
                try {
                    return searchModel.sortingDirection == ProductSearchModel.Order.ASC
                            ? field.get(p1).toString().compareTo(field.get(p2).toString())
                            : field.get(p2).toString().compareTo(field.get(p1).toString());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return 0;
        });
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(products)
                .build();
    }

    // получение верхней и нижней границ цен из полного списка описаний товров
    // - вариант вычисления в памяти
    public ResponseModel getProductsPriceBoundsInMemory() {
        Map<String, Integer> maxAndMin = new LinkedHashMap<>();
        // получаем из хранилища полный список описаний товаров
        // и определяем из него максимальную цену
        maxAndMin.put("min", productObjectifyDao.read().stream()
                .min((p1, p2) -> (int) (p1.getPrice() - p2.getPrice()))
                .map(product -> (int)Math.round(product.getPrice())).get());
        // получаем из хранилища полный список описаний товаров
        // и определяем из него минимальную цену
        maxAndMin.put("max", productObjectifyDao.read().stream()
                .max((p1, p2) -> (int) (p1.getPrice() - p2.getPrice()))
                .map(product -> (int)Math.round(product.getPrice())).get());
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(maxAndMin)
                .build();
    }

    // получение верхней и нижней границ цен из полного списка описаний товров
    // - вариант вычисления в хранилище
    @Override
    public ResponseModel getProductsPriceBounds() {
        Map<String, Integer> maxAndMin = new LinkedHashMap<>();
        // получаем из хранилища полный список описаний товаров
        // и определяем из него максимальную цену
        maxAndMin.put(
                "min",
                (int)Math.round(productObjectifyDao.getMin().doubleValue())
        );
        // получаем из хранилища полный список описаний товаров
        // и определяем из него минимальную цену
        maxAndMin.put(
                "max",
                (int)Math.round(productObjectifyDao.getMax().doubleValue())
        );
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(maxAndMin)
                .build();
    }
}
