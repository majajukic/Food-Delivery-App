package com.fooddeliveryapp.DeliveryService.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddeliveryapp.DeliveryService.models.DeliveryRequest;
import com.fooddeliveryapp.DeliveryService.services.DeliveryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/initiate-delivery")
    public ResponseEntity<Void> initiateDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest) {
        deliveryService.processDelivery(deliveryRequest);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}