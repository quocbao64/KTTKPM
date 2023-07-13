package com.example.stockservice.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order implements Serializable {
    @Id
    private String id;
    private String name;
    private int qty;
    private double price;

    @OneToOne(mappedBy = "order")
    @JsonIgnore
    private OrderEvent orderEvent;
}
