package com.example.fooddeliveryapp.OrderService.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.constants.OrderStatus;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryResponse;
import com.example.fooddeliveryapp.OrderService.external.models.DishResponse;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response model for an order.
 * This model is used to transfer data from the server
 * to the client when retrieving order record(s).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
	private UUID orderId;
	private LocalDateTime createdAt;
	private OrderStatus status;
	private Double amount;
	private List<DishResponse> dishes;
	private PaymentResponse paymentDetails;
	private DeliveryResponse deliveryDetails;
}
