package org.zakharov.springboot.google.appe.javaspringbootgoogleappe;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.net.httpserver.AuthFilter;

@Configuration
public class FiltersConfig {
    @Bean
    public FilterRegistrationBean<AuthFilter>
    authFilterRegistration() {
        final FilterRegistrationBean<AuthFilter> registration =
                new FilterRegistrationBean<>();
        registration.setFilter(new AuthFilter());
        registration.addUrlPatterns("/api/auth/users/*");
        registration.setOrder(1);
        return registration;
    }
}
