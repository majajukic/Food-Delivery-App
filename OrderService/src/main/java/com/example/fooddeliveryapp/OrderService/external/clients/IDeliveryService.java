package com.example.fooddeliveryapp.OrderService.external.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.fooddeliveryapp.OrderService.external.fallbacks.DeliveryServiceFallback;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryRequest;
import com.example.fooddeliveryapp.OrderService.external.models.DeliveryResponse;

import jakarta.validation.Valid;

/**
 * Feign client interface for interacting with the Delivery Service.
 * This interface is used to define communication with the Delivery Service's REST endpoints.
 * It simplifies the process of calling external microservices by using declarative REST client functionality provided by Feign.
 * 
 * The fallback method is implemented to handle service unavailability scenarios. 
 * If the Delivery Service is unavailable or an error occurs during the request, 
 * the fallback method will be triggered, providing a default behavior or response.
 */
@FeignClient(name = "DELIVERY-SERVICE/deliveries", fallback = DeliveryServiceFallback.class)
public interface IDeliveryService {
	@GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryDetailsByOrderId(@PathVariable UUID orderId);
    
	@PostMapping("/initiate-delivery")
    public ResponseEntity<UUID> initiateDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest);
	
	default void fallback(Exception ex) throws Exception {
		throw new Exception("Delivery service is currently not available");
	}
}
