package com.fooddeliveryservice.cloudgateway.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController { 

	@GetMapping("/restaurantServiceFallBack")
	public String restaurantServiceFallback() {
		return "Restaurant service is down. Please try again later.";
	}
	
	@GetMapping("/orderServiceFallBack")
	public String orderServiceFallback() {
		return "Order service is down. Please try again later.";
	}
	
	@GetMapping("/paymentServiceFallBack")
	public String paymentServiceFallback() {
		return "Payment service is down. Please try again later.";
	}
	
	@GetMapping("/deliveryServiceFallBack")
	public String deliveryServiceFallback() {
		return "Delivery service is down. Please try again later.";
	}
	
	@GetMapping("/userServiceFallBack")
	public String userServiceFallback() {
		return "User service is down. Please try again later.";
	}
}