package com.example.fooddeliveryapp.OrderService.external.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.fooddeliveryapp.OrderService.external.constants.DeliveryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a response model for a delivery.
 * This model is used to transfer data from the server
 * to the client when retrieving delivery record(s).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
	private UUID deliveryId; 
	private DeliveryStatus deliveryStatus; 
	private LocalDateTime initiatedAt;
	private LocalDateTime deliveredAt;
}
