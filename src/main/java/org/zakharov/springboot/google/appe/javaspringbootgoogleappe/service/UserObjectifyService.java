package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.dao.UserObjectifyDao;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ResponseModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.UserModel;

@Service
public class UserObjectifyService implements IUserService {

    @Autowired
    private UserObjectifyDao userObjectifyDao;

    @Override
    public ResponseModel create(UserModel userModel) {
        userObjectifyDao.create(userModel);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message(String.format("User %s Created", userModel.getName()))
                .build();
    }

    @Override
    public ResponseModel update(UserModel userModel) {
        userObjectifyDao.update(userModel);
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .message(String.format("User %s Updated", userModel.getName()))
                .build();
    }

    @Override
    public ResponseModel getAll() {
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(userObjectifyDao.read())
                .build();
    }

    @Override
    public ResponseModel delete(Long id) throws IllegalAccessException, InstantiationException {
        UserModel userModel = userObjectifyDao.read(id);
        if (userModel != null){
            userObjectifyDao.delete(userModel.getId());
            return ResponseModel.builder()
                    .status(ResponseModel.SUCCESS_STATUS)
                    .message(String.format("User #%s Deleted", userModel.getName()))
                    .build();
        } else {
            return ResponseModel.builder()
                    .status(ResponseModel.FAIL_STATUS)
                    .message(String.format("User #%d Not Found", id))
                    .build();
        }
    }

    /* получение данных учетной записи от сервера аутентификации гугл;
     * проверка: есть ли копия этих данных в хранилище DataStore,
     * и если есть - возврат этих данных, предварительно упакованных в модель ответа веб-клиенту,
     * а если нет - то же самое, с предварительным сохранением данных в хранилище DataStore */
    @Override
    public ResponseModel createOrGetUserByGoogleId(GoogleIdToken.Payload payload) throws Exception {
        // извлечение глобального идентификатора гугл-аккаунта
        String googleId = payload.getSubject();
        // попытка получить из хранилища данные учетной записи пользователя по его
        // идентификатору гугл-аккаунта
        UserModel userModel = userObjectifyDao.read(googleId);
        // если данные в хранилище не найдены
        if(userModel == null) {
            // создаем пустую модель пользователя
            userModel = new UserModel();
            // получаем данные аккаунта от гугл-сервера
            String email = payload.getEmail();
            // boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            // String locale = (String) payload.get("locale");
            // String familyName = (String) payload.get("family_name");
            // String givenName = (String) payload.get("given_name");
            // TODO use locale
            // сохранение пользователя в хранилище
            userModel.setGoogleId(googleId);
            userModel.setName(name);
            userModel.setEmail(email);
            userModel.setPictureUrl(pictureUrl);
            create(userModel);
        }
        return ResponseModel.builder()
                .status(ResponseModel.SUCCESS_STATUS)
                .data(userModel)
                .build();
    }
}
