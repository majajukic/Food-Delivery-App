package com.fooddeliveryapp.DeliveryService.services;

import java.util.UUID;

import com.fooddeliveryapp.DeliveryService.models.DeliveryRequest;
import com.fooddeliveryapp.DeliveryService.models.DeliveryResponse;

import jakarta.validation.Valid;

/**
 * Interface for the Delivery service that defines the operations 
 * related to delivery management. This interface acts as a contract 
 * for the implementation of delivery-related business logic.
 */
public interface IDeliveryService {
	DeliveryResponse getDeliveryByOrderId(UUID orderId);
	
	void processDelivery(@Valid DeliveryRequest deliveryRequest);
}
