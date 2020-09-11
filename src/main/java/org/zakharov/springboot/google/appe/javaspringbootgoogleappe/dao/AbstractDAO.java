package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;
/* Шаблонный абстракный класс логики доступа в хранилище
 * для чтения/изменения объектов сущностей любого типа,
 * который указывает каждыс кассом-потомком в качнестве параметра Т */
public abstract class AbstractDAO<T> {
    // объект рефлексии типа сущности
    private Class<T> entityType;
    public AbstractDAO() {
        // получаем объект рефлексии типа сущности, которым тот или иной
        // класс-потомок класса AbstractDAO<T> типизируется
        entityType =
                ((Class<T>) ((ParameterizedType) getClass()
                        .getGenericSuperclass()).getActualTypeArguments()[0]);
    }
    // добавление обного объекта сущности в хранилище
    public void create(T _entity) {
        // run - создает специальный параллельный поток выполнения,
        // в котором можно осуществлять чтение/запись в хранилище;
        // ofy - получение контеста хранилища
        // save - метод добавления или обновления объекта в хранилище;
        // entity - получает в качестве аргумента ссылку на объект сущности,
        // который нужно сохранить;
        // now выполняет операцию изменения данных немедленно
        ObjectifyService.run((Work) () -> ofy().save().entity(_entity).now());
    }
    // обновление уже существующего в хранилище объекта
    public void update(T _entity) {
        create(_entity);
    }
    // получение списка объектов сущности
    public List<T> read() {
        List<T> entities =
                (List<T>) ObjectifyService.run((Work) () -> {
                    // List<T> entitiesResult =
                    // load указывает, что необходимо чтение из хранилища,
                    // type принимет как аргумент объект рефлексии типа сущности,
                    // чтобы указать, какого типа объекты нужно получить из хранилища;
                    // list - указание немедленно получить список объектов типа
                    // entityType из хранилища
                    return ofy().load().type(entityType).list();
                });
        return entities;
    }
    // получение одного объекта сущности по его ИД
    public T read(Long _id)
            throws InstantiationException, IllegalAccessException {
        return ObjectifyService.run(() -> ofy().load().type(entityType).id(_id).now());
    }

    public void delete(Long _id) {
        ObjectifyService.run((Work) () ->
                ofy().delete().type(entityType).id(_id).now()
        );
    }
}