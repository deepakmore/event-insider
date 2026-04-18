package com.myproject.insider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import com.myproject.insider.filter.AuthFilter;

@Configuration
public class AuthFilterConfiguration {

    @Bean
    public AuthFilter authFilter(ObjectProvider<com.fasterxml.jackson.databind.ObjectMapper> objectMapperProvider,
                                 @Value("${app.security.require-sso-token:true}") boolean requireSsoToken,
                                 @Value("${app.security.sso-token:}") String expectedSsoToken) {
        if (requireSsoToken) {
            Assert.hasText(expectedSsoToken,
                    "app.security.sso-token must be configured when app.security.require-sso-token=true");
        }
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = objectMapperProvider
                .getIfAvailable(com.fasterxml.jackson.databind.ObjectMapper::new);
        return new AuthFilter(objectMapper, requireSsoToken, expectedSsoToken);
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(AuthFilter filter) {
        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.addUrlPatterns("/api/*");
        return registration;
    }
}