package com.example.orderservice.dto;

import com.example.orderservice.client.Product;
import lombok.Data;

@Data
public class OrderResponseDTO {
    private Integer id;
    private Integer userId;
    private Product product;
    private Double amount;
}
