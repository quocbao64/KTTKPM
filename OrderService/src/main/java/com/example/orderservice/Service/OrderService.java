package com.example.orderservice.Service;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OrderService {
    @Autowired
    private ProductClient productClient;
    public OrderResponseDTO placeOrder(OrderRequestDTO orderRequestDTO) {
        var product = productClient.getProduct(orderRequestDTO.getProductId());
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(new Random().nextInt(1000));
        orderResponseDTO.setAmount(orderResponseDTO.getAmount());
        orderResponseDTO.setUserId(orderRequestDTO.getUserId());
        orderResponseDTO.setProduct(product);
        return orderResponseDTO;
    }
}
