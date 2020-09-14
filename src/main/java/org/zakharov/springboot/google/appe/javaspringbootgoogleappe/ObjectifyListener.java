package org.zakharov.springboot.google.appe.javaspringbootgoogleappe;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.CategoryModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.ProductModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.SubscriptionModel;
import org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model.UserModel;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class ObjectifyListener implements ServletContextListener {

    private static final Logger log =
            Logger.getLogger(ObjectifyListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // при запуске веб-приложения подключиться к реальному или локальному эмулированному
        // хранилищу Google DataStore -
        // в зависимости от наличия или отсутствия ключа SPRING_PROFILES_ACTIVE
        // в файле src/main/webapp/WEB-INF/appengine-web.xml
        try {
            if (System.getenv("SPRING_PROFILES_ACTIVE") == null) {
                // local
                ObjectifyService.init(new ObjectifyFactory(
                        DatastoreOptions.newBuilder()
                                .setHost("http://localhost:8484")
                                .setProjectId("springboot-gae-simplespa")
                                .build()
                                .getService()
                ));
            }
            else {
                // GAE
                ObjectifyService.init(new ObjectifyFactory(
                        DatastoreOptions.getDefaultInstance().getService()
                ));
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage());
        }
        // регистрируем все классы моделей, экземпляры которых
        // затем будем читать или записывать в хранилище
        ObjectifyService.register(CategoryModel.class);
        ObjectifyService.register(ProductModel.class);
        ObjectifyService.register(UserModel.class);
        ObjectifyService.register(SubscriptionModel.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
