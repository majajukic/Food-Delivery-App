package com.fooddeliveryapp.DeliveryService.models;

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
	
	@NotNull(message = "User ID (email) cannot be null.")
    private String userId;
    
    @NotNull(message = "Restaurant ID cannot be null.")
    private UUID restaurantId;
}
