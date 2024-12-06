package com.fooddeliveryapp.DeliveryService.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddeliveryapp.DeliveryService.models.DeliveryRequest;
import com.fooddeliveryapp.DeliveryService.models.DeliveryResponse;
import com.fooddeliveryapp.DeliveryService.services.DeliveryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {
    @Autowired
    private DeliveryService deliveryService;
    
    /**
     * Endpoint to get delivery details by the order ID.
     * 
     * @param orderId The unique identifier of the order for which delivery details are being requested.
     * @return A ResponseEntity containing the delivery details (DeliveryResponse) and HTTP status code 200 (OK) if successful.
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryDetailsByOrderId(@PathVariable UUID orderId) {
    	DeliveryResponse deliveryResponse = deliveryService.getDeliveryByOrderId(orderId);
    	
    	return new ResponseEntity<>(deliveryResponse, HttpStatus.OK);
    }

    /**
     * Endpoint to initiate a delivery process.
     * 
     * @param deliveryRequest The request body containing the details of the delivery to be initiated.
     * @return A ResponseEntity with HTTP status code 200 (OK) to indicate the delivery process has been initiated.
     */
    @PostMapping("/initiate-delivery")
    public ResponseEntity<Void> initiateDelivery(@RequestBody @Valid DeliveryRequest deliveryRequest) {
        deliveryService.processDelivery(deliveryRequest);
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
}