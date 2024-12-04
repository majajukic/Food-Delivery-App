package com.example.fooddeliveryapp.OrderService.entities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Order entity in the database.
 * This class is mapped to a table in the database where each instance
 * of this class corresponds to an order record.
 */
@Entity
@Table(name = "\"order\"")
@Data // Generates getters, setters, toString(), equals(), and hashCode() methods automatically.
@AllArgsConstructor // Generates a constructor that accepts all fields as arguments.
@NoArgsConstructor // Generates a no-argument constructor, which is required by JPA.
@Builder 
public class Order {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private UUID orderId;
	
	//to do: make this not nullable when authentication is implemented
	@Column(name = "user_id", nullable = true)
    private UUID userId;
	
	@Column(name = "restaurant_id", nullable = false)
	private UUID restaurantId;
	
	@Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
