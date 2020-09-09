package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.CategoryDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.ProductDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.predicate.ProductPredicatesBuilder;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.*;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces.IProductService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductDao productObjectifyDao;

    @Autowired
    private CategoryDao categoryObjectifyDao;

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
        List<ProductModel> products = null;
        if (searchModel.searchString != null && !searchModel.searchString.isEmpty()) {
            Map<String, List<Long>> inMemoryFilterModel = new HashMap<>();
            // получаем из репозитория список моделей товаров,
            // отфильтрованных по всем критериям, кроме криериев на вхождение в множество знгачений
            List<ProductModel> productModels =
                    productObjectifyDao.getFiltered(searchModel, inMemoryFilterModel);
            // если есть хотя бы одно множество для фильтрации по нему -
            // осуществляем фильтрацию в памяти:
            // удаляем из списка товаров те, в которых поле категории не встречается
            // один из заданных идентификаторов
            inMemoryFilterModel.forEach((fieldName, valuesList) -> {
                if(valuesList != null && valuesList.size() > 0) {
                    productModels.removeIf((p) -> {
                        boolean categoryIdAbsents = true;
                        for (Long categoryId : valuesList) {
                            if(p.getCategoryId().equals(categoryId)) {
                                categoryIdAbsents = false;
                                break;
                            }
                        }
                        return categoryIdAbsents;
                    });
                }
            });


            /* BooleanExpression expression = builder.build();
            // выполнение sql-запроса к БД
            // с набором условий фильтрации
            // и с указанием имени поля и направления сортировки
            products =
                (List<Product>) productObjectifyDao.findAll(
                    expression,
                    Sort.by(
                        searchModel.sortingDirection,
                        searchModel.orderBy
                    )
                ); */
        } else {
            products =
                    productObjectifyDao.getFiltered(searchModel, null);
        }
        products = products.stream().map(productModel ->
                ProductModel.builder()
                        .title(productModel.getTitle())
                        .description(productModel.getDescription())
                        .priceDouble(productModel.getPrice().doubleValue())
                        .build()
        ).collect(Collectors.toList());
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
                .min((p1, p2) -> p1.getPrice().subtract(p2.getPrice())
                        .toBigInteger().intValue())
                .map(product -> (int)Math.round(product.getPrice().doubleValue())).get());
        // получаем из хранилища полный список описаний товаров
        // и определяем из него минимальную цену
        maxAndMin.put("max", productObjectifyDao.read().stream()
                .max((p1, p2) -> p1.getPrice().subtract(p2.getPrice())
                        .toBigInteger().intValue())
                .map(product -> (int)Math.round(product.getPrice().doubleValue())).get());
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
