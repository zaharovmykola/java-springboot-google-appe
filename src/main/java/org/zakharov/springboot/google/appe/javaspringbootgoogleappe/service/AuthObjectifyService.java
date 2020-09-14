package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service.interfaces.IAuthService;

@Service
public class AuthObjectifyService implements IAuthService {

    @Override
    public ResponseModel onSignOut() {
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message("Signed out")
                .build();
    }
}
