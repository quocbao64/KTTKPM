package com.example.stockservice.controller;


import com.example.stockservice.entity.Order;
import com.example.stockservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/getAllOrder")
    @Cacheable(value = "order")
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }
}
