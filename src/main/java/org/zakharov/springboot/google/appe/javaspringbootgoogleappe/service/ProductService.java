package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.CategoryDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.ProductDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.predicate.ProductPredicatesBuilder;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.*;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces.IProductService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    public ResponseModel create(ProductModel productModel) {
        productDao.create(productModel);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message(String.format("Product %s Created", productModel.getTitle()))
                .build();
    }

    public ResponseModel update(ProductModel productModel) {
            productDao.update(productModel);
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(String.format("Product %s Updated", productModel.getTitle()))
                    .build();
    }

    public ResponseModel getAll() {
        return ResponseModel.builder()
            .status(ResponseModel.SUCCESS_STATUS)
            .data(productDao.read())
            .build();
    }

    public ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException {
        ProductModel productModel = productDao.read(id);
        if (productModel != null){
            productDao.delete(productModel.getId());
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(String.format("Product #%s Deleted", productModel.getTitle()))
                    .build();
        } else {
            return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("Product #%d Not Found", id))
                    .build();
        }
    }

    @Override
    public ResponseModel getFiltered(ProductFilterModel filter) {
        List<ProductModel> products =
                productDao.getFiltered(filter);
        if(filter.categories != null && filter.categories.size() > 0) {
            products.removeIf((p) -> {
                boolean categoryIdAbsents = true;
                for (Long categoryId : filter.categories) {
                    if(p.getCategoryId().equals(categoryId)) {
                        categoryIdAbsents = false;
                        break;
                    }
                }
                return categoryIdAbsents;
            });
        }
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(products)
                .build();
    }


    // поиск отфильтрованного и отсортированного списка товаров
    // на основе запросов query dsl
    public ResponseModel search(ProductSearchModel searchModel) {
        List<ProductModel> products = null;
        if (searchModel.searchString != null && !searchModel.searchString.isEmpty()) {
            ProductPredicatesBuilder builder = new ProductPredicatesBuilder();
            // разбиение значения http-параметра search
            // на отдельные выражения условий фильтрации
            Pattern pattern = Pattern.compile("([\\w]+?)(:|<|>|<:|>:)([\\w\\]\\[\\,]+?);");
            Matcher matcher = pattern.matcher(searchModel.searchString + ";");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
            /////////////////////////////////////////////////////////////////////////////////
            Pattern patternSquareBrakets = Pattern.compile("([]\]+?)");
            Matcher matcherSquareBrakets = pattern.matcher(searchModel.searchString + ";");
            if (matcher.find()) {
                if(filter.categories != null && filter.categories.size() > 0) {
                    products.removeIf((p) -> {
                        boolean categoryIdAbsents = true;
                        for (Long categoryId : filter.categories) {
                            if(p.getCategoryId().equals(categoryId)) {
                                categoryIdAbsents = false;
                                break;
                            }
                        }
                        return categoryIdAbsents;
                    }
            } else {
                builder.with(matcher.group(1));
            }
            /////////////////////////////////////////////////////////////////////////////////

            // TODO завершите построение метода фильтра,
            // для этого пропустите через цикл весь список критерий,
            // и для тех, в значении которых не встречается квадратная скобка -
            // примените предикат,
            // иначе - запланируйте фильтрацию по списку идентификаторов категорий прямо
            // здесь: установите флаг "по категориям" в истину,
            // и когда вызовете срабатывание объекта запроса -
            // отфильтруйте его результаты в памяти:

            /* if(filter.categories != null && filter.categories.size() > 0) {
                products.removeIf((p) -> {
                    boolean categoryIdAbsents = true;
                    for (Long categoryId : filter.categories) {
                        if(p.getCategoryId().equals(categoryId)) {
                            categoryIdAbsents = false;
                            break;
                        }
                    }
                    return categoryIdAbsents;
                });
            } */

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
                    productDao.findAll(
                            Sort.by(
                                    searchModel.sortingDirection,
                                    searchModel.orderBy
                            )
                    );
        }
        return getResponseModelFromEntities(products);
    }

    /*private ResponseModel getResponseModelFromEntities(List<Product> products) {
        List<ProductModel> productModels =
                products.stream()
                        .map((p)->
                                ProductModel.builder()
                                        .id(p.getId())
                                        .title(p.getName())
                                        .description(p.getDescription())
                                        .price(p.getPrice())
                                        .quantity(p.getQuantity())
                                        .image(p.getImage())
                                        .category(
                                                CategoryModel.builder()
                                                        .id(p.getCategory().getId())
                                                        .name(p.getCategory().getName())
                                                        .build()
                                        )
                                        .build()
                        )
                        .collect(Collectors.toList());
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(productModels)
                .build();
    }*/

    /*public ResponseModel getProductsPriceBounds() {
        Map<String, Integer> maxndMin = new LinkedHashMap<>();
        maxndMin.put("min", productDao.findMinimum().intValue());
        maxndMin.put("max", productDao.findTop1ByOrderByPriceDesc().getPrice().intValue());
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(maxndMin)
                .build();
    }*/

}
