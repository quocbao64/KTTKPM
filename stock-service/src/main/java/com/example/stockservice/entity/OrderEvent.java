package com.example.stockservice.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_event")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String status;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}