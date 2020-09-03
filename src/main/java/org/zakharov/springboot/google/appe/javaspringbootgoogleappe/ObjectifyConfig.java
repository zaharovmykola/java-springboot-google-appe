package org.zakharov.springboot.google.appe.javaspringbootgoogleappe;

import com.googlecode.objectify.ObjectifyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectifyConfig {
    @Bean
    public FilterRegistrationBean<ObjectifyFilter>
    objectifyFilterRegistration() {
        final FilterRegistrationBean<ObjectifyFilter> registration =
                new FilterRegistrationBean<>();
        registration.setFilter(new ObjectifyFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
    @Bean
    public ServletListenerRegistrationBean<ObjectifyListener>
    listenerRegistrationBean() {
        ServletListenerRegistrationBean<ObjectifyListener> bean
                = new ServletListenerRegistrationBean<>();
        bean.setListener(new ObjectifyListener());
        return bean;
    }
}
