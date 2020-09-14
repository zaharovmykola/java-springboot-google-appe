package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Work;
import org.springframework.stereotype.Repository;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.SubscriptionModel;

import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class SubscriptionObjectifyDao extends AbstractDAO<SubscriptionModel> {
    //  поиск в хранилище объекта типа SubscriptionModel по идентификатору категории и
    // идентификатору подписчика
    public SubscriptionModel findSubscriberByCategoryIdAndSubscriberId(Long categoryId, Long subscriberId) throws Exception {
        return (SubscriptionModel) ObjectifyService.run(
                (Work) () -> ofy().load()
                        .type(SubscriptionModel.class)
                        .filter("categoryId", categoryId)
                        .filter("subscriberId", subscriberId)
                        .first()
                        .now()
        );
    }
    public List<SubscriptionModel> getSubscriberListByCategoryId(Long categoryId) throws Exception {
        return (List<SubscriptionModel>) ObjectifyService.run(
                (Work) () -> ofy().load()
                        .type(SubscriptionModel.class)
                        .filter("categoryId", categoryId)
                        .list()
        );
    }
}
