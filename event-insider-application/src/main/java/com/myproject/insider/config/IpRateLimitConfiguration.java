package com.myproject.insider.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.insider.filter.IpRateLimitFilter;
import com.myproject.insider.service.IpRateLimitService;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class IpRateLimitConfiguration {

    @Bean
    public IpRateLimitService ipRateLimitService(RateLimitProperties properties, StringRedisTemplate redisTemplate) {
        return new IpRateLimitService(properties, redisTemplate);
    }

    @Bean
    public FilterRegistrationBean<IpRateLimitFilter> ipRateLimitFilterRegistration(
            ObjectProvider<ObjectMapper> objectMapperProvider,
            IpRateLimitService rateLimitService) {
        FilterRegistrationBean<IpRateLimitFilter> registration = new FilterRegistrationBean<>(
                new IpRateLimitFilter(objectMapperProvider.getIfAvailable(ObjectMapper::new), rateLimitService));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 5);
        registration.addUrlPatterns("/api/*");
        return registration;
    }
}
