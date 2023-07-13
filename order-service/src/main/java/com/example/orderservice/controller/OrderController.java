package com.example.orderservice.controller;

import com.example.basedomain.entity.Order;
import com.example.basedomain.entity.OrderEvent;
import com.example.orderservice.logging.LogExecutionTime;
import com.example.orderservice.producer.OrderProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private OrderProducer orderProducer;
    private RestTemplate restTemplate;

    public OrderController(OrderProducer orderProducer) {
        this.orderProducer = orderProducer;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/orders")
    public String placeOrder(@RequestBody Order order){

        order.setId(UUID.randomUUID().toString());

        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setStatus("PENDING");
        orderEvent.setMessage("order status is in pending state");
        orderEvent.setOrder(order);

        orderProducer.sendMessage(orderEvent);

        return "Order placed successfully ...";
    }

    @GetMapping("")
    @LogExecutionTime
    public ResponseEntity<Order[]> getAllOrder() {
        return restTemplate
                .getForEntity("http://localhost:8081/api/v1/stock/getAllOrder",
                        Order[].class);
    }
}
