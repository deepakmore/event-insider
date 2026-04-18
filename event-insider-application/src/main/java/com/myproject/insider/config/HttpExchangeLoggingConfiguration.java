package com.myproject.insider.config;

import com.myproject.insider.filter.HttpExchangeLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class HttpExchangeLoggingConfiguration {

    @Bean
    public FilterRegistrationBean<HttpExchangeLoggingFilter> httpExchangeLoggingFilterRegistration() {
        FilterRegistrationBean<HttpExchangeLoggingFilter> registration =
                new FilterRegistrationBean<>(new HttpExchangeLoggingFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.addUrlPatterns("/*");
        return registration;
    }
}