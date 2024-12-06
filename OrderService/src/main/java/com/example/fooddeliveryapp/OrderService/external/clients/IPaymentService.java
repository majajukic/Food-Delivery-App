package com.example.fooddeliveryapp.OrderService.external.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.fooddeliveryapp.OrderService.external.fallbacks.PaymentServiceFallback;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentRequest;
import com.example.fooddeliveryapp.OrderService.external.models.PaymentResponse;

import jakarta.validation.Valid;

/**
 * Feign client interface for interacting with the Payment Service.
 * This interface is used to define communication with the Payment Service's REST endpoints.
 * It simplifies the process of calling external microservices by using declarative REST client functionality provided by Feign.
 * 
 * The fallback method is implemented to handle service unavailability scenarios. 
 * If the Payment Service is unavailable or an error occurs during the request, 
 * the fallback method will be triggered, providing a default behavior or response.
 */
@FeignClient(name = "PAYMENT-SERVICE/payments", fallback = PaymentServiceFallback.class)
public interface IPaymentService {
	@PostMapping
	ResponseEntity<UUID> pay(@RequestBody @Valid PaymentRequest paymentRequest);
	
	@GetMapping("{orderId}")
	ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable UUID orderId);
}
