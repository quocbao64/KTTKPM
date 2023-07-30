package com.example.orderservice.client;

import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ProductClient {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${api.catalog.baseUrl}")
    private String baseUrl;

    public Product getProduct(int productId) {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(10)
                .waitDuration(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("productService", config);
        return retry.executeSupplier(() -> restTemplate.getForObject(baseUrl + "/api/v1/products/" + productId, Product.class));
    }
}
