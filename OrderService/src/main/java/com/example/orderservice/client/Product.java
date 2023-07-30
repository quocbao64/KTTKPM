package com.example.orderservice.client;

import lombok.Data;

@Data
public class Product {
    private Integer id;
    private String name;
    private Double price;
}
