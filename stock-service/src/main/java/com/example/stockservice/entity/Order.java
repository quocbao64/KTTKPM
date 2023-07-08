package com.example.stockservice.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    private String id;
    private String name;
    private int qty;
    private double price;

    @OneToOne(mappedBy = "order")
    private OrderEvent orderEvent;
}
