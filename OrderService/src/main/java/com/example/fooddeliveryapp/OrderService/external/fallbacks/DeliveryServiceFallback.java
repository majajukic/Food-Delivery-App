package com.example.fooddeliveryapp.OrderService.external.fallbacks;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.fooddeliveryapp.OrderService.external.clients.IDeliveryService;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryRequest;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryResponse;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

/**
 * Fallback implementation of the IDeliveryService interface.
 * This class is invoked when the Delivery Service is unavailable or when there is an error
 * while interacting with the Delivery Service via Feign.
 */
@Component
@Log4j2
public class DeliveryServiceFallback implements IDeliveryService {

	 /**
     * Fallback method for getDeliveryDetailsByOrderId. 
     * This method is triggered when the Delivery Service cannot be reached or encounters an error.
     * It logs the error and returns an HTTP 503 status with no body.
     * 
     * @param orderId The ID of the order for which delivery details are requested.
     * @return A ResponseEntity with a 503 status and no body, indicating service unavailability.
     */
	@Override
	public ResponseEntity<DeliveryResponse> getDeliveryDetailsByOrderId(UUID orderId) {
		log.error("Fallback triggered for getDeliveryDetailsByOrderId");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
	}

	/**
     * Fallback method for initiateDelivery. 
     * This method is triggered when the Delivery Service cannot be reached or encounters an error.
     * It logs the error and returns an HTTP 503 status with no body.
     * 
     * @param deliveryRequest The delivery request containing the initiation details.
     * @return A ResponseEntity with a 503 status and no body, indicating service unavailability.
     */
	@Override
	public ResponseEntity<UUID> initiateDelivery(@Valid DeliveryRequest deliveryRequest) {
		log.error("Fallback triggered for initiateDelivery");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
	}
}