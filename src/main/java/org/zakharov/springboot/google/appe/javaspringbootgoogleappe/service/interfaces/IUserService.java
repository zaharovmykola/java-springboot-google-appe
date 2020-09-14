package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.UserModel;

public interface IUserService {
    ResponseModel create(UserModel userModel);
    ResponseModel update(UserModel userModel);
    ResponseModel getAll();
    ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException;
    ResponseModel createOrGetUserByGoogleId(GoogleIdToken.Payload payload) throws Exception;
}
