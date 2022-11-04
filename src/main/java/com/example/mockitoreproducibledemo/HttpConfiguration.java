package com.example.mockitoreproducibledemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
class HttpConfiguration {
    @Value("${http.client.timeout_ms}")
    int timeoutMs;

    @Value("${http.client.max_attempts}")
    int maximumAttempts;

    @Value("${http.client.exponential_backoff.initial_interval_ms}")
    int initialIntervalMs;

    @Value("${http.client.exponential_backoff.multiplier}")
    int multiplier;

    @Value("${http.client.exponential_backoff.max_interval_ms}")
    int maximumIntervalMs;

    @Bean
    HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    @Bean
    RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(maximumAttempts)
                .exponentialBackoff(initialIntervalMs, multiplier, maximumIntervalMs, true)
                .retryOn(RuntimeException.class)
                .build();
    }
}
