package com.exadel.tenderflex.config;

import com.exadel.tenderflex.repository.cache.CacheStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheStorage<Object> tokenCache() {
        return new CacheStorage<>(360, TimeUnit.SECONDS);
    }
}
