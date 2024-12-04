package com.example.fooddeliveryapp.OrderService.models;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Represents a request to place a new order with order items
 * This model is used to transfer data from the client to the server
 * when creating a new order record.
 */
@Data
public class OrderRequest {
	private UUID userId; // will be null until authentication is implemented
	
	@NotNull(message = "Restaurant ID must not be null")
    private UUID restaurantId;
	
	@NotEmpty(message = "Order items must not be empty")
    private List<@NotNull(message = "Each order item must not be null")OrderItemRequest> items;
}
