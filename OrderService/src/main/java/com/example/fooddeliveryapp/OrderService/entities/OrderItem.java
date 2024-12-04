package com.example.fooddeliveryapp.OrderService.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an OrderItem entity in the database.
 * This class is mapped to a table in the database where each instance
 * of this class corresponds to an order item record.
 */
@Entity
@Data // Generates getters, setters, toString(), equals(), and hashCode() methods automatically.
@AllArgsConstructor // Generates a constructor that accepts all fields as arguments.
@NoArgsConstructor // Generates a no-argument constructor, which is required by JPA.
@Builder 
public class OrderItem {
 	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_item_id")
    private UUID orderItemId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "dish_id", nullable = false)
    private UUID dishId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;
}
