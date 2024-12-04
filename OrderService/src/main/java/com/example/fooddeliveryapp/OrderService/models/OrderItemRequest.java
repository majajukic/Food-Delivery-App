package com.example.fooddeliveryapp.OrderService.models;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Represents a request to add a new order item to an order.
 * This model is used to transfer data from the client to the server
 * when creating a new order item record.
 */
@Data
public class OrderItemRequest {
	@NotNull(message = "Dish ID must not be null")
	private UUID dishId;
	
	@NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
	
	@NotNull(message = "Price must not be null")
    @Positive(message = "Price must be a positive value")
    private Double price; // to-do: get this value from restaurant service (Dish price) once the communication is established
}
