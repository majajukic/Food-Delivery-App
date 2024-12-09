package com.example.fooddeliveryapp.OrderService.models;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to add a new order item to an order.
 * This model is used to transfer data from the client to the server
 * when creating a new order item record.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {
	@NotNull(message = "Dish ID must not be null")
	private UUID dishId;
	
	@NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
