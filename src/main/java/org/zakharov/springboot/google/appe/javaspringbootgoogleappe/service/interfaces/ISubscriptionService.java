package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces;

import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.SubscriptionModel;

public interface ISubscriptionService {
    ResponseModel create(SubscriptionModel subscriptionModel);
    ResponseModel getByCategoryId(Long categoryId) throws Exception;
    ResponseModel delete(Long categoryId, Long subscriberId) throws Exception;
}
