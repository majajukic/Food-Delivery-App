package com.fooddeliveryapp.DeliveryService.services;

import com.fooddeliveryapp.DeliveryService.models.DeliveryRequest;

import jakarta.validation.Valid;

/**
 * Interface for the Delivery service that defines the operations 
 * related to delivery management. This interface acts as a contract 
 * for the implementation of delivery-related business logic.
 */
public interface IDeliveryService {
	void processDelivery(@Valid DeliveryRequest deliveryRequest);
}
