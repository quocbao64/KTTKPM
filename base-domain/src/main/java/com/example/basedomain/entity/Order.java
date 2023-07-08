package com.example.basedomain.entity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private String name;
    private int qty;
    private double price;
}