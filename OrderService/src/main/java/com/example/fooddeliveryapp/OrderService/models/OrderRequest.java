package com.example.fooddeliveryapp.OrderService.models;

import java.util.List;
import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.external.constants.PaymentMode;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to place a new order with order items
 * This model is used to transfer data from the client to the server
 * when creating a new order record.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {	
	@NotNull(message = "Restaurant ID must not be null")
    private UUID restaurantId;
	
	@NotNull(message = "Payment mode should not be null")
	private PaymentMode paymentMode;
	
	@NotEmpty(message = "Order items must not be empty")
    private List<@NotNull(message = "Each order item must not be null")OrderItemRequest> items;
}
