package com.juvarya.nivaas.core.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public FeignClientInterceptor customFeignClientInterceptor() {
        return new FeignClientInterceptor();
    }
}
