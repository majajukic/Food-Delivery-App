package com.example.fooddeliveryapp.OrderService.external.models;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request model for initiating a delivery.
 * This model is used to transfer necessary order information
 * to the delivery service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryRequest {
	@NotNull(message = "Order ID cannot be null.")
    private UUID orderId;
	
    private UUID userId; // can be null until authentication is implemented
    
    @NotNull(message = "Restaurant ID cannot be null.")
    private UUID restaurantId;
}
