package com.vishwa.jobms.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignJwtInterceptor {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getCredentials() instanceof String token) {
                template.header("Authorization", "Bearer " + token);
            }
        };
    }
}
