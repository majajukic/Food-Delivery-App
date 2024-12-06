package com.fooddeliveryapp.DeliveryService.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fooddeliveryapp.DeliveryService.constants.DeliveryStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a Delivery entity in the database.
 * This class is mapped to a table in the database where each instance
 * of this class corresponds to an delivery record.
 */
@Entity
@Data // Generates getters, setters, toString(), equals(), and hashCode() methods automatically.
@AllArgsConstructor // Generates a constructor that accepts all fields as arguments.
@NoArgsConstructor // Generates a no-argument constructor, which is required by JPA.
@Builder 
public class DeliveryDetails {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "delivery_id")
	    private UUID deliveryId; 

	    @Column(name = "order_id", nullable = false)
	    private UUID orderId; 

	    @Column(name = "restaurant_id", nullable = false)
	    private UUID restaurantId;

	    @Column(name = "user_id", nullable = true) // can be null until authentication is implemented
	    private UUID userId;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "delivery_status", nullable = false)
	    private DeliveryStatus deliveryStatus; 

	    @Column(name = "initiated_at", nullable = false)
	    private LocalDateTime initiatedAt;

	    @Column(name = "delivered_at", nullable = true)
	    private LocalDateTime deliveredAt;
}
