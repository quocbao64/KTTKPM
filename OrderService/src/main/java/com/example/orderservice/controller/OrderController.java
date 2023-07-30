package com.example.orderservice.controller;

import com.example.orderservice.Service.OrderService;
import com.example.orderservice.client.Product;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("")
    @RateLimiter(name = "rateLimiterApi")
//    @CircuitBreaker(name = "order", fallbackMethod = "getProductFallback")
    public OrderResponseDTO postOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.placeOrder(orderRequestDTO);
    }

    public OrderResponseDTO getProductFallback(Exception exception) {
        log.error("Unable to get order: " + exception.getMessage());
        return null;
    }
}
