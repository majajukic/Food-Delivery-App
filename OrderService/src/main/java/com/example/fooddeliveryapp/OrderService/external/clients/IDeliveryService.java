package com.example.fooddeliveryapp.OrderService.external.clients;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.fooddeliveryapp.OrderService.external.models.DeliveryRequest;

import jakarta.validation.Valid;

@FeignClient(name = "DELIVERY-SERVICE/deliveries")
public interface IDeliveryService {
	@PostMapping("/initiate-delivery")
    public ResponseEntity<UUID> initiateDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest);
}
