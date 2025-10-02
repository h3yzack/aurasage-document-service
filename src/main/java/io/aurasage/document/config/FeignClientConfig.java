package io.aurasage.document.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import feign.RequestInterceptor;

@Configuration
@EnableFeignClients(basePackages = "io.aurasage.document.client")
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                String token = jwtAuth.getToken().getTokenValue();
                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }

    @Bean
    public HttpMessageConverters messageConverters() {
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }
}
