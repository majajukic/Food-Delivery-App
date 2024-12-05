package com.example.fooddeliveryapp.OrderService.external.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.fooddeliveryapp.OrderService.external.models.PaymentRequest;

import jakarta.validation.Valid;

/**
 * Feign client interface for interacting with the Payment Service.
 * This interface is used to define communication with the Payment Service's REST endpoints.
 * It simplifies the process of calling external microservices by using declarative REST client functionality provided by Feign.
 */
@FeignClient(name = "PAYMENT-SERVICE/payments")
public interface IPaymentService {
	@PostMapping
	ResponseEntity<UUID> pay(@RequestBody @Valid PaymentRequest paymentRequest);
}
